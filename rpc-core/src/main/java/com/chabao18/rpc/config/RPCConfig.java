package com.chabao18.rpc.config;

import cn.hutool.core.lang.copier.SrcToDestCopier;
import com.chabao18.rpc.serializer.SerializerKeys;
import lombok.Data;

@Data
public class RPCConfig {
    private String name = "JetCall";

    private String version = "1.0";

    private String serverHost = "localhost";

    private Integer serverPort = 8080;

    private boolean mock = false;

    private String serializer = SerializerKeys.JDK;
}
