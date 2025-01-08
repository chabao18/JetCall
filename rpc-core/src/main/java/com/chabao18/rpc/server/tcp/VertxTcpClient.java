package com.chabao18.rpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.chabao18.rpc.RPCApplication;
import com.chabao18.rpc.model.RPCRequest;
import com.chabao18.rpc.model.RPCResponse;
import com.chabao18.rpc.model.ServiceMetaInfo;
import com.chabao18.rpc.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
public class VertxTcpClient {

    public void start() {
        Vertx vertx = Vertx.vertx();

        vertx.createNetClient().connect(8888, "localhost", res -> {
            if (res.succeeded()) {
                System.out.println("connected to tcp server");
                NetSocket socket = res.result();
                socket.write("Hello from tcp client!");
                socket.handler(buffer -> {
                    System.out.println("received response from server: " + buffer.toString());
                });
            } else {
                System.err.println("failed to connect tcp server: " + res.cause().getMessage());
            }
        });
    }

    public static RPCResponse doRequest(RPCRequest rpcRequest, ServiceMetaInfo smi) throws InterruptedException, ExecutionException {
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<RPCResponse> future = new CompletableFuture<>();
        netClient.connect(smi.getServicePort(), smi.getServiceHost(),
                result -> {
                    if (!result.succeeded()) {
                        log.error("failed to connecte to tcp server");
                        return;
                    }

                    NetSocket socket = result.result();

                    // 发送数据，构造消息
                    ProtocolMessage<RPCRequest> protocolMessage = new ProtocolMessage<>();
                    ProtocolMessage.Header header = new ProtocolMessage.Header();
                    header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                    header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                    header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RPCApplication.getRpcConfig().getSerializer()).getKey());
                    header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                    header.setRequestId(IdUtil.getSnowflakeNextId());
                    protocolMessage.setHeader(header);
                    protocolMessage.setBody(rpcRequest);

                    // 编码请求
                    try {
                        Buffer buffer = ProtocolMessageEncoder.encode(protocolMessage);
                        socket.write(buffer);
                    } catch (IOException e) {
                        throw new RuntimeException("protocol message encode failed");
                    }

                    // 接受回应
                    TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(
                            buffer -> {
                                try {
                                    ProtocolMessage<RPCResponse> resp = (ProtocolMessage<RPCResponse>) ProtocolMessageDecoder.decode(buffer);
                                    future.complete(resp.getBody());
                                } catch (IOException e) {
                                    throw new RuntimeException("protocol message decode failed");
                                }
                            }
                    );
                    socket.handler(bufferHandlerWrapper);

                });
        RPCResponse rpcResponse = future.get();
        netClient.close();
        return rpcResponse;
    }

    public static void main(String[] args) {
        new VertxTcpClient().start();
    }
}
