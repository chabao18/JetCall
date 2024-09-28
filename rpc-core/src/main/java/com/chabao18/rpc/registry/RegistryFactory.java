package com.chabao18.rpc.registry;

import com.chabao18.rpc.spi.SPILoader;

public class RegistryFactory {
    static {
        SPILoader.load(Registry.class);
    }

    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    public static Registry getInstance(String key) {
        return SPILoader.getInstance(Registry.class, key);
    }

}
