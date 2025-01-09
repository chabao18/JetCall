package com.chabao18.rpc.registry;



import com.chabao18.rpc.config.RegistryConfig;
import com.chabao18.rpc.model.ServiceMetaInfo;


public interface Registry {

    void init(RegistryConfig registryConfig);

    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    void unRegister(ServiceMetaInfo serviceMetaInfo);

    ServiceMetaInfo serviceDiscovery(String serviceKey);

    void destroy();

    void heartBeat();

    void watch(String serviceNodeKey);
}
