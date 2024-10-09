package com.chabao18.rpc.protocol;

import cn.hutool.core.util.ObjectUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ProtocolMessageSerializerEnum {
    JDK(0, "jdk"),
    JSON(1, "json"),
    PROTOBUF(2, "protobuf"),
    HESSION(3, "hession");

    private final int key;

    private final String value;

    ProtocolMessageSerializerEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    public static ProtocolMessageSerializerEnum getEnumByKey(int key) {
        for (ProtocolMessageSerializerEnum serializerEnum : ProtocolMessageSerializerEnum.values()) {
            if (serializerEnum.key == key) {
                return serializerEnum;
            }
        }
        return null;
    }

    public static ProtocolMessageSerializerEnum getEnumByValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (ProtocolMessageSerializerEnum serializerEnum : ProtocolMessageSerializerEnum.values()) {
            if (serializerEnum.value.equals(value)) {
                return serializerEnum;
            }
        }
        return null;
    }
}
