package com.xilidou.wsproxy

import io.vertx.core.Handler
import io.vertx.core.eventbus.EventBus
import io.vertx.core.http.ServerWebSocket
import org.slf4j.LoggerFactory

class WebSocketHandler(private val eventBus: EventBus) : Handler<ServerWebSocket> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun handle(webSocket: ServerWebSocket) {
        val binaryHandlerID = webSocket.binaryHandlerID()
        val consumer = eventBus.consumer<String>("callback") { message ->
            val body = message.body()
            log.info("send message {}", body)
            webSocket.writeTextMessage(body)
        }
        webSocket.endHandler() {
            log.info("end", binaryHandlerID)
            consumer.unregister();
        }
        webSocket.writeTextMessage("欢迎使用 xilidou 钉钉 代理")
        webSocket.writeTextMessage("连接成功")
    }
}

