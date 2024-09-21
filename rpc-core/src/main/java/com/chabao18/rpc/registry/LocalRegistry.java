package com.chabao18.rpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalRegistry {
    private static final Map<String, Class<?>> map = new ConcurrentHashMap<>();

    // add service
    public static void register(String serviceName, Class<?> implClass) {
        map.put(serviceName, implClass);
    }

    // get service
    public static Class<?> get(String serviceName) {
        return map.get(serviceName);
    }

    // remove service
    public static void remove(String serviceName) {
        map.remove(serviceName);
    }

}
