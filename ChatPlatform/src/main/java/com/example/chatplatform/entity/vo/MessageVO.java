package com.example.chatplatform.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author archi
 */
@Data
public class MessageVO {
    private Long id;
    private String senderId;
    private String receiverId;

    private String content;
    //消息类型 0:文字 1:图片 2:文件 3:语音 4:撤回消息
    private Integer type;
    private String mediaUrl;
    private Long mediaSize;
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;
}
