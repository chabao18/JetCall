package com.chabao18.rpc.protocol;

import com.chabao18.rpc.model.RPCRequest;
import com.chabao18.rpc.model.RPCResponse;
import com.chabao18.rpc.serializer.Serializer;
import com.chabao18.rpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

public class ProtocolMessageDecoder {


    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException {
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        byte magic = buffer.getByte(0);

        if (magic != ProtocolConstant.PROTOCOL_MAGIC) {
            throw new RuntimeException("magic number is illegal");
        }
        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        header.setRequestId(buffer.getLong(5));
        header.setBodyLength(buffer.getInt(13));

        byte[] bodyBytes = buffer.getBytes(17, 17 + header.getBodyLength());

        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (serializerEnum == null) {
            throw new RuntimeException("serializer protocol does not exist");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum.getEnumByKey(header.getType());
        if (messageTypeEnum == null) {
            throw new RuntimeException("message type does not exist");
        }
        switch (messageTypeEnum) {
            case REQUEST:
                RPCRequest request = serializer.deserialize(bodyBytes, RPCRequest.class);
                return new ProtocolMessage<>(header, request);
            case RESPONSE:
                RPCResponse response = serializer.deserialize(bodyBytes, RPCResponse.class);
                return new ProtocolMessage<>(header, response);
            case HEART_BEAT:
            case OTHERS:
            default:
                throw new RuntimeException("does not support this message type");
        }
    }

}
