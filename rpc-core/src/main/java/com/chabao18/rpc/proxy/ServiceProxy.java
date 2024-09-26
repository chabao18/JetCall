package com.chabao18.rpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.chabao18.rpc.RPCApplication;
import com.chabao18.rpc.model.RPCRequest;
import com.chabao18.rpc.model.RPCResponse;
import com.chabao18.rpc.model.ServiceMetaInfo;
import com.chabao18.rpc.serializer.JDKSerializer;
import com.chabao18.rpc.serializer.Serializer;
import com.chabao18.rpc.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // choose serializer
        final Serializer serializer = SerializerFactory.getInstance(RPCApplication.getRpcConfig().getSerializer());

        String serviceName = method.getDeclaringClass().getName();
        // make request
        RPCRequest rpcRequest = RPCRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            // send request
            // todo need Service Discovery
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                         .body(bodyBytes)
                         .execute()) {
                byte[] result = httpResponse.bodyBytes();
                // deserialize
                RPCResponse rpcResponse = serializer.deserialize(result, RPCResponse.class);
                return rpcResponse.getData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}