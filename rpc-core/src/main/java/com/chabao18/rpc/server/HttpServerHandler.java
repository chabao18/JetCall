package com.chabao18.rpc.server;

import com.chabao18.rpc.model.RPCRequest;
import com.chabao18.rpc.model.RPCResponse;
import com.chabao18.rpc.registry.LocalRegistry;
import com.chabao18.rpc.serializer.JDKSerializer;
import com.chabao18.rpc.serializer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;

@Slf4j
public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {
        // choose serializer
        final Serializer serializer = new JDKSerializer();

        log.info("Received request: {} {}", request.method(), request.uri());

        // handle http request
        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RPCRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes, RPCRequest.class);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // make response
            RPCResponse rpcResponse = new RPCResponse();
            if (rpcResponse == null) {
                rpcResponse.setMessage("rpcRequest is null");
                doResponse(request, rpcResponse, serializer);
                return;
            }

            try {
                // get service name
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());

                // make response
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            // do response
            doResponse(request, rpcResponse, serializer);
        });

    }

    void doResponse(HttpServerRequest request, RPCResponse rpcResponse, Serializer serializer) {
        HttpServerResponse httpServerResponse = request.response().putHeader("content-type", "application/json");
        try {
            // serialize
            byte[] serialized = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
