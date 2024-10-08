package com.chabao18.rpc.registry;

import com.chabao18.rpc.model.ServiceMetaInfo;

import java.util.List;

public class RegistryServiceCache {
    List<ServiceMetaInfo> serviceCache;

    void writeCache(List<ServiceMetaInfo> serviceCache) {
        this.serviceCache = serviceCache;
    }

    List<ServiceMetaInfo> readCache() {
        return this.serviceCache;
    }

    void clearCache() {
        this.serviceCache = null;
    }
}
