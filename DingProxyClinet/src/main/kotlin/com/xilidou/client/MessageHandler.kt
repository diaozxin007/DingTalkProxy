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

    val Hook =
        "https://oapi.dingtalk.com/robot/send?access_token=1d43c26cf6d2938ffa4454297fbd71389f34ec1b1869ca8818c9c5cc8bc7f58f"

    override fun handle(event: String) {
        log.info("event $event")
        val jsonObject = JsonObject(event)
        val sessionWebhook = jsonObject.getString("sessionWebhook")
        val TextContent = jsonObject.getJsonObject("text").getString("content")


//        Wechaty bot = Wechaty.instance();
        val client = DefaultDingTalkClient(Hook)

        val request = OapiRobotSendRequest()

        val text: OapiRobotSendRequest.Text = OapiRobotSendRequest.Text()
        text.content = "TEST $TextContent"
        request.msgtype = "text"
        request.setText(text);

        val execute: OapiRobotSendResponse = client.execute(request)

    }

}
