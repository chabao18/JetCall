package com.chabao18.rpc.protocol;

import lombok.Getter;

@Getter
public enum ProtocolMessageTypeEnum {
    REQUEST(0),
    RESPONSE(1),
    HEART_BEAT(2),
    OTHERS(3);

    private final int key;
    ProtocolMessageTypeEnum(int key) {
       this.key = key;
   }

   public static ProtocolMessageTypeEnum getEnumByKey(int key) {
       for (ProtocolMessageTypeEnum typeEnum : ProtocolMessageTypeEnum.values()) {
           if (typeEnum.key == key) {
               return typeEnum;
           }
       }
       return null;
   }
}
