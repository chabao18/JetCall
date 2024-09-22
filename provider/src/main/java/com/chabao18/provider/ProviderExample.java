package com.chabao18.provider;

import com.chabao18.common.service.UserService;
import com.chabao18.rpc.RPCApplication;
import com.chabao18.rpc.registry.LocalRegistry;
import com.chabao18.rpc.server.VertxHttpServer;

public class ProviderExample {
    public static void main(String[] args) {
        // inti rpc
        RPCApplication.init();

        // register service
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // start web server
        VertxHttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);
    }
}
