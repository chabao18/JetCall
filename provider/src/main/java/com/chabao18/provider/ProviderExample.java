package com.chabao18.provider;

import com.chabao18.rpc.server.VertxHttpServer;

public class ProviderExample {
    public static void main(String[] args) {
        VertxHttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);
    }
}
