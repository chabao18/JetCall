package com.chabao18.rpc.serializer;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import java.io.IOException;

public class ProtobufSerializer implements Serializer {

    @Override
    public <T> byte[] serialize(T object) throws IOException {
        if (object instanceof Message) {
            return ((Message) object).toByteArray();  // Protobuf对象序列化为字节数组
        } else {
            throw new IllegalArgumentException("Object is not a valid Protobuf message.");
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        try {
            // 确保类型是 Protobuf 消息类型
            if (Message.class.isAssignableFrom(type)) {
                Message.Builder builder = (Message.Builder) type.getMethod("newBuilder").invoke(null);
                builder.mergeFrom(bytes);  // 从字节数组反序列化为 Protobuf 对象
                return (T) builder.build();
            } else {
                throw new IllegalArgumentException("Type is not a valid Protobuf message class.");
            }
        } catch (InvalidProtocolBufferException e) {
            throw new IOException("Failed to deserialize Protobuf message", e);
        } catch (ReflectiveOperationException e) {
            throw new IOException("Failed to instantiate Protobuf message", e);
        }
    }
}

