package com.chabao18.rpc.server.tcp;

import com.chabao18.rpc.model.RPCRequest;
import com.chabao18.rpc.model.RPCResponse;
import com.chabao18.rpc.protocol.*;
import com.chabao18.rpc.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import java.io.IOException;
import java.lang.reflect.Method;

public class TcpServerHandler implements Handler<NetSocket> {

    @Override
    public void handle(NetSocket socket) {
        TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
            // 接受请求，解码
            ProtocolMessage<RPCRequest> protocolMessage;
            try {
                protocolMessage = (ProtocolMessage<RPCRequest>) ProtocolMessageDecoder.decode(buffer);
            } catch (IOException e) {
                throw new RuntimeException("协议消息解码错误");
            }
            RPCRequest RPCRequest = protocolMessage.getBody();
            ProtocolMessage.Header header = protocolMessage.getHeader();

            // 处理请求
            // 构造响应结果对象
            RPCResponse RPCResponse = new RPCResponse();
            try {
                // 获取要调用的服务实现类，通过反射调用
                Class<?> implClass = LocalRegistry.get(RPCRequest.getServiceName());
                Method method = implClass.getMethod(RPCRequest.getMethodName(), RPCRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), RPCRequest.getArgs());
                // 封装返回结果
                RPCResponse.setData(result);
                RPCResponse.setDataType(method.getReturnType());
                RPCResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                RPCResponse.setMessage(e.getMessage());
                RPCResponse.setException(e);
            }

            // 发送响应，编码
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
            header.setStatus((byte) ProtocolMessageStatusEnum.OK.getValue());
            ProtocolMessage<RPCResponse> responseProtocolMessage = new ProtocolMessage<>(header, RPCResponse);
            try {
                Buffer encode = ProtocolMessageEncoder.encode(responseProtocolMessage);
                socket.write(encode);
            } catch (IOException e) {
                throw new RuntimeException("协议消息编码错误");
            }
        });
        socket.handler(bufferHandlerWrapper);
    }

}