package com.chabao18.rpc;

import com.chabao18.rpc.config.RPCConfig;
import com.chabao18.rpc.constant.RPCConstant;
import com.chabao18.rpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RPCApplication {
    private static volatile RPCConfig rpcConfig;

    public static void init(RPCConfig config) {
        rpcConfig = config;
        log.info("RPC init, config = {}", rpcConfig.toString());
    }

    public static void init() {
        RPCConfig config;
        try {
            config = ConfigUtils.loadConfig(RPCConfig.class, RPCConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            // use default
            config = new RPCConfig();
        }
        init(config);
    }


    public static RPCConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RPCApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
