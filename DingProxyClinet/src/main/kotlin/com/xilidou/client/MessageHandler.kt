package com.xilidou.client

import com.dingtalk.api.DefaultDingTalkClient
import com.dingtalk.api.request.OapiRobotSendRequest
import com.dingtalk.api.response.OapiRobotSendResponse
import io.vertx.core.Handler
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory

class MessageHandler (private val eventBus: EventBus): Handler<String> {

    val log = LoggerFactory.getLogger(this.javaClass);

    val Hook = ""


    override fun handle(event: String) {
        log.info("event $event")
        val jsonObject = JsonObject(event)
        val TextContent = jsonObject.getJsonObject("text").getString("content")

        val client = DefaultDingTalkClient(Hook)

        val request = OapiRobotSendRequest()

        val text: OapiRobotSendRequest.Text = OapiRobotSendRequest.Text()
        text.content = "TEST $TextContent"
        request.msgtype = "text"
        request.setText(text);

        val execute: OapiRobotSendResponse = client.execute(request)

    }

}
