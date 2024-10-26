package com.example.chatcommon.models;

import lombok.Data;

/**
 * @author archi
 */
@Data
/**
 * 检测websocket是否成功建立
 */
public class AskWebSocketReadyMsg {
    private Integer terminal;
}
