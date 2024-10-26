package com.example.chatserver;

import com.example.chatserver.ws.WebSocketChatServer;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


/**
 * @author archi
 */
@Component
//实现CommandLineRunner run 方法将在 SpringBoot 应用启动后自动执行。
public class ChatServerRunner implements CommandLineRunner {
    @Resource
    private WebSocketChatServer webSocketChatServer;

    @Override
    public void run(String... args) throws Exception {
        webSocketChatServer.start();
    }
    @PreDestroy
    void stop(){
        webSocketChatServer.stop();
    }
}
