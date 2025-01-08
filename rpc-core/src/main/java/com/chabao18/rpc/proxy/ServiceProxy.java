package com.chabao18.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.chabao18.rpc.RPCApplication;
import com.chabao18.rpc.config.RPCConfig;
import com.chabao18.rpc.constant.RPCConstant;
import com.chabao18.rpc.model.RPCRequest;
import com.chabao18.rpc.model.RPCResponse;
import com.chabao18.rpc.model.ServiceMetaInfo;
import com.chabao18.rpc.protocol.*;
import com.chabao18.rpc.registry.Registry;
import com.chabao18.rpc.registry.RegistryFactory;
import com.chabao18.rpc.serializer.JDKSerializer;
import com.chabao18.rpc.serializer.Serializer;
import com.chabao18.rpc.serializer.SerializerFactory;
import com.chabao18.rpc.server.tcp.VertxTcpClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // choose serializer
        final Serializer serializer = SerializerFactory.getInstance(RPCApplication.getRpcConfig().getSerializer());

        // make request
        String serviceName = method.getDeclaringClass().getName();
        RPCRequest rpcRequest = RPCRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest);

            // get service address from registry
            RPCConfig rpcConfig = RPCApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RPCConstant.DEFAULT_SERVICE_VERSION);
            // fix bug
            log.info("service key: {}", serviceMetaInfo.getServiceKey());
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("no service available");
            }
            ServiceMetaInfo smi = serviceMetaInfoList.get(0);


            // send http request
            try (HttpResponse httpResponse = HttpRequest.post(smi.getServiceAddress())
                         .body(bodyBytes)
                         .execute()) {
                byte[] result = httpResponse.bodyBytes();
                // deserialize
                RPCResponse rpcResponse = serializer.deserialize(result, RPCResponse.class);
                return rpcResponse.getData();
            }

            // todo[tcp]: send tcp request
//            RPCResponse rpcResponse = VertxTcpClient.doRequest(rpcRequest, smi);
//            return rpcResponse.getData();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}