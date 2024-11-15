package com.example.chatplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAsync
@EnableTransactionManagement
@EnableScheduling
//@PropertySource(value={"classpath:application.yml"})
public class ChatPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatPlatformApplication.class, args);
    }

}
