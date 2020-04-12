“山川异域，风月同钉”，被钉钉暴打的你，是不是已经想写一个机器人调戏一下钉钉了。在写机器人的时候，钉钉机器人的回调需要填写一个公网 http 地址。

这还没开发机器人，就没有 http 服务，没有 http 服务就收不到钉钉的回调，没有回调就不能调试机器人。不能调试机器人，就不能上线。

![black](https://img.xilidou.com/img/black.jpg)

<!--more-->

又一次陷入了被钉钉暴打的死循环，办法总比问题多，所以为了解决这个问题。我们就需要一个公网代理。所以我们就来撸一个。

这里注意一下，由于一般开发人员都处在内网环境。要想让代理做内网穿透，技术比较复杂。所以我们就换个思路。我们可以利用 Websocket 的双工的特性。接入代理，当代理收到钉钉的回调的时候，把消息推倒我们本地开发环境。提升我们开发的效率。见下图：

![dingproxy.jpg](https://img.xilidou.com/img/dingproxy.jpg)

## 使用方法

```bash
    git clone https://github.com/diaozxin007/DingTalkProxy
    cd DingProxyServer
    ./gradlew build
    java -jar build/libs/dingWs-all.jar
    # 如果需要在后台运行
    nohup java -jar build/libs/dingWs-1.0.0-all.jar &>> nohup.out & tailf nohup.out
```

可以修改 resources 下的 `server.properties`

```shell
    # 监听端口
    server.port=8080
    # 钉钉回调的 uri
    server.api=/ding/api
```

然后重新运行:

```shell
    ./gradlew build
```

这个时候，proxy 已经开始正常运行了。

如果只是想看看一看钉钉回调的报文，那就可以直接使用 [websock-test] ([http://www.websocket-test.com/](http://www.websocket-test.com/)) GUI 调试工具。

如果想在代码里面使用可以参考 DingProxyClinet 里面的代码。

## 注意事项

Q:1、为什么我连不上服务？

A:确认服务是否只开启了 https，如果开启了 https, 需要把协议头修改为 wss。

Q:2、我还是连不上？

A:需要确认 nginx 的配置，是否支持 WebSocket。

可以在 nginx 的配置中增加

```shell
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection "Upgrade";
    # 如果频繁超时断开可以配置
    proxy_connect_timeout 7d;
    proxy_send_timeout 7d;
    proxy_read_timeout 7d;
```

Q:3、除了做钉钉的代理，还能干什么？

A: 理论上可以代理一切请求，然后装换为 String 通过 WebSocket 推送到客户端。 

Q:4、我懒得部署服务了

A：可以使用我提供的公益服务

在回调接口中填写：

- https://api.xilidou.com/ding/api

WebSocket 地址为:

- wss://api.xilidou.com

为了防止滥用，每个客户端每次连接只能接收 10 条消息，然后会被断开。

下一篇文章将会具体讲解，如何使用 vertx 实现这个代理。敬请期待。
