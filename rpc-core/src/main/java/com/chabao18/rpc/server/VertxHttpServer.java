package com.chabao18.rpc.server;

import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VertxHttpServer implements HttpServer {

    @Override
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();

        //handle request
        server.requestHandler(request -> {
            // handle http request
            log.info("Received request: {} {}", request.method(), request.uri());

            // send http response
            request.response().putHeader("content-type", "text/plain")
                    .end("Hello from Vert.x HTTP server!");
        });

        // start http server and listen on port
        server.listen(port, result -> {
            if (result.succeeded()) {
                log.info("Server is now listening on port {}", port);
            } else {
                log.info("Fail to start server: {}", result.cause());
            }
        });
    }

    public static void main(String[] args) {
        log.info("hello world test test2");
    }
}
