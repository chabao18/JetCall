package com.chabao18.provider;

import cn.hutool.core.lang.copier.SrcToDestCopier;
import com.chabao18.common.service.UserService;
import com.chabao18.rpc.RPCApplication;
import com.chabao18.rpc.config.RPCConfig;
import com.chabao18.rpc.config.RegistryConfig;
import com.chabao18.rpc.model.ServiceMetaInfo;
import com.chabao18.rpc.registry.LocalRegistry;
import com.chabao18.rpc.registry.Registry;
import com.chabao18.rpc.registry.RegistryFactory;
import com.chabao18.rpc.server.VertxHttpServer;

public class ProviderExample {
    public static void main(String[] args) {
        // init rpc
        RPCApplication.init();

        // register service
        String serviceName = UserService.class.getName();
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // init registry
        RPCConfig rpcConfig = RPCApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // start web server
        VertxHttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RPCApplication.getRpcConfig().getServerPort());

    }

    public static void localMain() {
        // inti rpc
        RPCApplication.init();

        // register service
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // start web server
        VertxHttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);
    }
}
