package com.example.chatserver.ws.endecode;

import com.example.chatcommon.models.SendInfo;
import com.example.chatcommon.utils.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author archi
 */
@Slf4j
public class MessageProtocolEncoder extends MessageToMessageEncoder<SendInfo> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, SendInfo sendInfo, List<Object> list) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String text = objectMapper.writeValueAsString(sendInfo);
        TextWebSocketFrame frame = new TextWebSocketFrame(text);
        list.add(frame);
    }
}
