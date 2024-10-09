package com.chabao18.rpc.server;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VertxTcpServer implements HttpServer{
    private byte[] handleRequest(byte[] request) {
        // TODO: handle request
        return "Hello from Vert.x TCP server!".getBytes();
    }

    @Override
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();
        NetServer server = vertx.createNetServer();

        // handle request
        server.connectHandler(socket -> {
            // handle tcp request
            socket.handler(buffer -> {
                byte[] request = buffer.getBytes();
                byte[] response = handleRequest(request);
                socket.write(Buffer.buffer(response));
            });
        });

        // start tcp server and listen on port
        server.listen(port, result -> {
            if (result.succeeded()) {
                log.info("Server is now listening on port {}", port);
            } else {
                log.info("Fail to start tcp server");
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }
}
