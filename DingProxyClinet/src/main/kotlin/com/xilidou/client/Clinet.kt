package com.xilidou.client

import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.core.http.HttpClientOptions
import org.slf4j.LoggerFactory

class Client : AbstractVerticle() {

    val log = LoggerFactory.getLogger(this.javaClass);

    override fun start() {

        val eb = vertx.eventBus();

        val httpClientOptions = HttpClientOptions()

        httpClientOptions.isSsl = true

        val client = vertx.createHttpClient(httpClientOptions)

        client.webSocket(443, "api.xilidou.com", "/") { websocket ->
            if (websocket.succeeded()) {
                val result = websocket.result()
                result.textMessageHandler(MessageHandler(eb))
            }
        }


    }
}

fun main(){
    val vertx = Vertx.vertx()
    vertx.deployVerticle(Client())
}
