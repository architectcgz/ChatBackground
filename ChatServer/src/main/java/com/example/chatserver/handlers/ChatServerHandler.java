package com.example.chatserver.handlers;

import com.example.chatcommon.enums.NettyCmdType;
import com.example.chatcommon.models.SendInfo;
import com.example.chatserver.MsgCmdHandlers.CommandHandler;
import com.example.chatserver.MsgCmdHandlers.CommandHandlerFactory;
import com.example.chatserver.UserChannelCtxMap;
import com.example.chatserver.constants.ChannelAttrKeys;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

/**
 * @author archi
 */
@Slf4j
public class ChatServerHandler extends SimpleChannelInboundHandler<SendInfo> {
    static final String TOKEN_REGEX = "token=([^&]+)";
    static Pattern PATTERN = Pattern.compile(TOKEN_REGEX);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("用户 " + ctx.channel().id().asShortText() + "的websocket连接已建立");
    }



    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                // 在规定时间内没有收到客户端的上行数据, 主动断开连接
                AttributeKey<Long> attr = AttributeKey.valueOf("USER_ID");
                Long userId = ctx.channel().attr(attr).get();
                AttributeKey<Integer> terminalAttr = AttributeKey.valueOf(ChannelAttrKeys.TERMINAL_TYPE);
                Integer terminal = ctx.channel().attr(terminalAttr).get();
                log.info("心跳超时，即将断开连接,用户id:{},终端类型:{} ", userId, terminal);
                ctx.channel().close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SendInfo msg) throws Exception {
        log.info("服务器接收到command: "+msg.getCmd());
        NettyCmdType cmd =  NettyCmdType.fromCode(msg.getCmd());
        CommandHandler handler = null;
        if (cmd != null) {
            handler = CommandHandlerFactory.getHandler(cmd);
            if (handler != null) {
                handler.handle(ctx,msg);
            }else{
                log.info("无法处理这种类型的消息. cmd: "+cmd);
            }
        }else{
            log.info("无法处理这种类型的消息. cmd: "+msg.getCmd());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info(cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info(ctx.channel().id().asShortText()+"尝试连接");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        AttributeKey<String> userIdAttr = AttributeKey.valueOf(ChannelAttrKeys.USER_ID);
        String userUid = String.valueOf(ctx.channel().attr(userIdAttr).get());
        AttributeKey<Integer> terminalAttr = AttributeKey.valueOf(ChannelAttrKeys.TERMINAL_TYPE);
        Integer terminal = ctx.channel().attr(terminalAttr).get();
        ChannelHandlerContext context = UserChannelCtxMap.getChannelCtx(userUid,terminal);
        // 判断一下，避免异地登录导致的误删
        if (context != null && ctx.channel().id().equals(context.channel().id())) {
            log.info("Before removing: userId: {}, terminal type: {}, channel count: {}", userUid, terminal, UserChannelCtxMap.getUserChannelCount(userUid));
            // 移除channel
            UserChannelCtxMap.removeContext(userUid, terminal);
            log.info("After removing: userId: {}, terminal type: {}, channel count: {}", userUid, terminal, UserChannelCtxMap.getUserChannelCount(userUid));
            // 用户下线
            log.info("断开连接,userId:{},终端类型:{},终端id:{}", userUid, terminal, ctx.channel().id().asShortText());
        }
    }
}
