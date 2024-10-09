package com.chabao18.rpc.protocol;

public enum ProtocolMessageTypeEnum {
    REQUEST(0),
    RESPONSE(1),
    HEARTBEAT(2),
    OTHER(3);

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
