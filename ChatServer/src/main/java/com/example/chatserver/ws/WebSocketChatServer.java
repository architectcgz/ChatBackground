package com.example.chatserver.ws;

import com.example.chatserver.handlers.ChatServerHandler;
import com.example.chatserver.handlers.HeartBeatHandler;
import com.example.chatserver.ws.endecode.MessageProtocolDecoder;
import com.example.chatserver.ws.endecode.MessageProtocolEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author archi
 */
@Slf4j
@Component
public class WebSocketChatServer {
    @Value("${websocket.port}")
    private int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private volatile boolean ready = false;


    public void start(){
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //添加http协议的codec
                            pipeline.addLast(new HttpServerCodec());
                            //用于支持异步写大量数据流并且不需要消耗大量内存也不会导致内存溢出错误
                            pipeline.addLast(new ChunkedWriteHandler());
                            //设置最大聚合消息的大小 64KB
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            pipeline.addLast(new WebSocketServerProtocolHandler("/ws",null,true,65536,true,true,10000L));
                            //服务器未接收到消息即超时,此时断开服务器与客户端的链接
                            //用HeartBeatHandler来处理
                            /*
                                如果客户端1min内没有向服务端发送读心跳 则主动断开连接
                             */
                            pipeline.addLast(new IdleStateHandler(60,0,0, TimeUnit.SECONDS));
                            pipeline.addLast(new HeartBeatHandler());
                            pipeline.addLast(new MessageProtocolDecoder());
                            pipeline.addLast(new MessageProtocolEncoder());
                            pipeline.addLast(new ChatServerHandler());
                        }
                    })
                    // backlog表示主线程池中在套接口排队的最大数量，队列由未连接队列（三次握手未完成的）和已连接队列
                    .option(ChannelOption.SO_BACKLOG,5)
                    //启用TCP的保持活动连接功能，以便保持连接的活跃状态  TCP会定期发送探测数据包，以确保连接在空闲时不会被断开
                    .childOption(ChannelOption.SO_KEEPALIVE,true);
                ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
                log.info("ChatServer has been started");
                this.ready = true;
                channelFuture.channel().closeFuture().sync();
        }catch (InterruptedException e){
            log.info("ChatServer initialize error");
            e.printStackTrace();
        }
    }
    public void stop(){
        if(bossGroup!=null&&!bossGroup.isShutdown()&&!bossGroup.isShuttingDown()){
            bossGroup.shutdownGracefully();
        }
        if(workerGroup!=null&& !workerGroup.isShutdown()&&!workerGroup.isShuttingDown()){
            workerGroup.shutdownGracefully();
        }
        log.info("ChatServer has been shutdown");
    }

    public boolean isReady(){
        return this.ready;
    }
}
