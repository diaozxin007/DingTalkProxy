package com.xilidou.wsproxy

import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.ext.web.Router

class DingVerticleV2: AbstractVerticle(){

    override fun start() {
        val eventBus = vertx.eventBus()
        val httpServer = vertx.createHttpServer()

        val router = Router.router(vertx);
        router.post("/api/ding").handler(HttpHandler(eventBus));
        httpServer.requestHandler(router);

        httpServer.webSocketHandler(WebSocketHandler(eventBus));
        httpServer.listen(8080);
    }
}

fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(DingVerticleV2())
}
