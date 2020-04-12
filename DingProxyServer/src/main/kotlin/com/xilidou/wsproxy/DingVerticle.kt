package com.xilidou.wsproxy

import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.core.http.ServerWebSocket
import io.vertx.ext.web.Router
import org.slf4j.LoggerFactory

class WebVerticle : AbstractVerticle() {

    private val log = LoggerFactory.getLogger(this.javaClass)
    override fun start() {

        val httpServer = vertx.createHttpServer()
        val router = Router.router(vertx)
        router.post("/ding/api").handler { event ->
            val request = event.request()
            request.bodyHandler { t ->
                println(t)
            }
            event.response().end();
        }
        httpServer.requestHandler(router);

        httpServer.webSocketHandler { webSocket: ServerWebSocket ->
            val binaryHandlerID = webSocket.binaryHandlerID()
            webSocket.endHandler() {
                log.info("end", binaryHandlerID)
            }
            webSocket.writeTextMessage("欢迎使用 xilidou 钉钉 代理")
            webSocket.writeTextMessage("连接成功")
        }
        httpServer.listen(8080);

    }
}


fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(WebVerticle())
}
