package com.chabao18.rpc.protocol;

import lombok.Getter;

@Getter
public enum ProtocolMessageStatusEnum {
    OK("ok", 20),
    BAD_REQUEST("bad request", 40),
    BAD_RESPONSE("bad response", 50);

    private final String text;
    private final int value;

    ProtocolMessageStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    public static ProtocolMessageStatusEnum getEnumByValue(int value) {
        for (ProtocolMessageStatusEnum statusEnum : ProtocolMessageStatusEnum.values()) {
            if (statusEnum.value == value) {
                return statusEnum;
            }
        }
        return null;
    }
}
