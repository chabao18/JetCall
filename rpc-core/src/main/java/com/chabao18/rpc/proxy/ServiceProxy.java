package com.chabao18.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.chabao18.rpc.RPCApplication;
import com.chabao18.rpc.config.RPCConfig;
import com.chabao18.rpc.constant.RPCConstant;
import com.chabao18.rpc.loadbalancer.ConsistentHashLoadBalancer;
import com.chabao18.rpc.loadbalancer.LoadBalancer;
import com.chabao18.rpc.loadbalancer.RRLoadBalancer;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ServiceProxy implements InvocationHandler {
    private static final int MAX_RETRIES = 5;
    private static final int INITIAL_RETRY_DELAY_MS = 1000;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 拦截 Object 的方法（idea 打断点查看对象时会调用toString方法，也会调用invoke，因此要忽略掉）
        if (method.getDeclaringClass() == Object.class) {
            if ("toString".equals(method.getName())) {
                return proxy.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(proxy));
            }
            if ("hashCode".equals(method.getName())) {
                return System.identityHashCode(proxy);
            }
            if ("equals".equals(method.getName()) && args != null && args.length == 1) {
                return proxy == args[0];
            }
        }
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

            log.debug("Service Key: {}", serviceMetaInfo.getServiceKey());
            ServiceMetaInfo smi = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (smi == null) {
                throw new RuntimeException("No service available");
            }

            return sendReqWithRetry(smi.getServiceAddress(), bodyBytes, serializer);

            // todo[tcp]: send tcp request
//            RPCResponse rpcResponse = VertxTcpClient.doRequest(rpcRequest, smi);
//            return rpcResponse.getData();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // todo: 自定义retry机制，retry作为参数传入
    private Object sendReqWithRetry(String serviceAddress, byte[] bodyBytes, Serializer serializer) {
        int retryCount = 0;
        int retryDelayMs = INITIAL_RETRY_DELAY_MS;

        while (retryCount < MAX_RETRIES) {
            try {

                // 模拟失败
                // if (retryCount <= 1)
                //    throw new IOException("Simulated network error");

                try (HttpResponse httpResponse = HttpRequest.post(serviceAddress)
                        .body(bodyBytes)
                        .timeout(5000)
                        .execute()) {
                    byte[] result = httpResponse.bodyBytes();
                    // deserialize
                    RPCResponse rpcResponse = serializer.deserialize(result, RPCResponse.class);
                    return rpcResponse.getData();
                }
            } catch (IOException e) {
                log.error("Request failed (attempt {} of {}), error: {}", retryCount + 1, MAX_RETRIES, e.getMessage());
                if (retryCount > MAX_RETRIES) {
                    throw new RuntimeException("RPC request failed after " + MAX_RETRIES + " attempts");
                }

                try {
                    Thread.sleep(retryDelayMs);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt(); // 恢复中断状态
                    throw new RuntimeException("Thread interrupted during retry delay", ex);
                }

                retryDelayMs += retryDelayMs;
                retryCount++;
            }
        }
        return null;
    }
}