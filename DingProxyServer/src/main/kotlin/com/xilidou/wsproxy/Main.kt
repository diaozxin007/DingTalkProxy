package com.xilidou.wsproxy

import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.http.ServerWebSocket
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import org.slf4j.LoggerFactory


class WebSocketVerticle : AbstractVerticle() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun start() {

        val options = ConfigStoreOptions()
            .setFormat("properties")
            .setType("file")
            .setConfig(
                JsonObject().put("path", "server.properties")
            )

        val retriever = ConfigRetriever.create(vertx, ConfigRetrieverOptions().addStore(options))

        val promise = Promise.promise<JsonObject>()

        retriever.getConfig {ar-> promise.handle(ar)}

        val eb = vertx.eventBus()
        val httpServer = vertx.createHttpServer()
            .webSocketHandler { webSocket: ServerWebSocket ->
            run {
                val binaryHandlerID = webSocket.binaryHandlerID()
                log.info("binary id {}",binaryHandlerID)
                val consumer = eb.consumer<String>("callback") { message ->
                    run {
                        val body = message.body()
                        println(binaryHandlerID)
                        log.info("send message {}",body)
                        webSocket.writeTextMessage(body)
                    }
                }
                webSocket.endHandler(){
                    log.info("end",binaryHandlerID)
                    consumer.unregister();
                }

            }
        }


        val okPromise = Promise.promise<String>()
        promise.future().setHandler{ ar->
            run {
                if (ar.succeeded()) {
                    val router = Router.router(vertx);
                    router.post( promise.future().result().getString("server.api")).handler(DingHandler(eb))
                    httpServer.requestHandler(router);
                    httpServer.listen( promise.future().result().getInteger("server.port")) {
                        okPromise.complete()
                    }
                }
            }
        }

        okPromise.future().setHandler{ ar ->
            run {
                if (ar.succeeded()) {
                    log.info("ok")
                }
            }
        }



    }

    override fun getVertx(): Vertx {
        return vertx;
    }
}


fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(WebSocketVerticle())
}

