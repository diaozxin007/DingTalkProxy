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

        // https 开关
        httpClientOptions.isSsl = false

        val client = vertx.createHttpClient(httpClientOptions)

        client.webSocket(8080, "127.0.0.1", "/") { websocket ->
            if (websocket.succeeded()) {
                val result = websocket.result()
                result.textMessageHandler(MessageHandler(eb))
            }

            if(websocket.failed()){
                println(websocket.cause())
            }
        }


    }
}

fun main(){
    val vertx = Vertx.vertx()
    vertx.deployVerticle(Client())
}
