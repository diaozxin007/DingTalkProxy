package com.xilidou.wsproxy

import io.vertx.core.Handler
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory

class DingHandler(private val eventBus: EventBus) : Handler<RoutingContext> {

    private val log = LoggerFactory.getLogger(this.javaClass);

    override fun handle(event: RoutingContext) {

        val request = event.request()

        request.bodyHandler { t->
            run {
                val jsonObject = JsonObject(t)
                val toString = jsonObject.toString()

                log.info("request is {}",toString);

                eventBus.publish("callback", toString)
            }
        }

        event.response().end("ok")

    }


}