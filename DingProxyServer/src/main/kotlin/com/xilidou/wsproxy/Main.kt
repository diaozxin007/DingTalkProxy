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
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger


class DingVerticle : AbstractVerticle() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val countMap = ConcurrentHashMap<String, AtomicInteger>();

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

                        val check = check(binaryHandlerID)

                        val body = message.body()
                        println(binaryHandlerID)
                        log.info("send message {}",body)
                        webSocket.writeTextMessage(body)

                        if(!check){
                            webSocket.writeTextMessage("超过 10 次网络将会断开")
                            webSocket.close()
                        }

                    }
                }
                webSocket.endHandler(){
                    log.info("end",binaryHandlerID)
                    consumer.unregister();
                }

                webSocket.writeTextMessage("欢迎使用 xilidou 钉钉 代理")
                webSocket.writeTextMessage("连接成功")

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

    fun check(id:String):Boolean{

        val count = countMap.getOrDefault(id, AtomicInteger());

        val flag =  count.addAndGet(1) <= 10

        if(flag){
            countMap[id] = count
        }else{
            countMap.remove(id);
        }

        log.info("count id $id and flag is $flag")

        return flag




    }
}


fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(DingVerticle())
}

