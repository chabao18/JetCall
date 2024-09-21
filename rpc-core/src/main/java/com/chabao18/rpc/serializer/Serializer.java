package com.chabao18.rpc.serializer;

import java.io.IOException;

public interface Serializer {
    <T> byte[] serialize(T object) throws IOException;

    <T> T deserialize(byte[] bytes, Class<T> type) throws IOException;
}
