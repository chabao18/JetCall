package com.chabao18.rpc.server.tcp;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetSocket;

public class VertxTcpClient {

    public void start() {
        Vertx vertx = Vertx.vertx();

        vertx.createNetClient().connect(8888, "localhost", res -> {
            if (res.succeeded()) {
                System.out.println("connected to tcp server");
                NetSocket socket = res.result();
                socket.write("Hello from tcp client!");
                socket.handler(buffer -> {
                    System.out.println("received response from server: " + buffer.toString());
                });
            } else {
                System.err.println("failed to connect tcp server: " + res.cause().getMessage());
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpClient().start();
    }
}
