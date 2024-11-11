package com.example.chatserver.MsgCmdHandlers;

import com.example.chatcommon.constants.ChatRedisKeys;
import com.example.chatcommon.constants.ChatSystemConstants;
import com.example.chatcommon.enums.NettyCmdType;
import com.example.chatcommon.models.ConnectInfo;
import com.example.chatcommon.models.SendInfo;
import com.example.chatcommon.utils.JwtUtils;
import com.example.chatserver.UserChannelCtxMap;
import com.example.chatserver.constants.ChannelAttrKeys;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ConnectHandler implements CommandHandler{
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public void handle(ChannelHandlerContext ctx, SendInfo msg) {
        ObjectMapper objectMapper = new ObjectMapper();
        ConnectInfo connectInfo;
        try {
            String jsonStr = objectMapper.writeValueAsString(msg.getData());
            connectInfo = objectMapper.readValue(jsonStr,ConnectInfo.class);
        }catch (JsonProcessingException e){
            log.info(e.getMessage());
            return;
        }

        String accessToken = connectInfo.getAccessToken();
        String platformStr = JwtUtils.getPlatformFromToken(accessToken);
        if(platformStr == null){
            SendInfo sendInfo = new SendInfo();
            sendInfo.setCmd(NettyCmdType.TOKEN_EXPIRED.getCode());
            sendInfo.setData("token 已过期");
            ctx.channel().writeAndFlush(sendInfo);
            ctx.channel().close();
            return;
        }
        Integer platform = Integer.valueOf(platformStr);
        if (!validateToken(platform,accessToken)) {
            SendInfo sendInfo = new SendInfo();
            sendInfo.setCmd(NettyCmdType.TOKEN_EXPIRED.getCode());
            ctx.channel().writeAndFlush(sendInfo);
            ctx.channel().close();
            log.warn("用户token校验不通过，强制下线,token:{}", accessToken);
            return;
        }

        String userId = JwtUtils.getUserIdFromToken(accessToken);

        log.info("用户连接，userId:{}", userId);
        ChannelHandlerContext context = UserChannelCtxMap.getChannelCtx(userId, platform);
        if (context != null && !ctx.channel().id().equals(context.channel().id())) {
            // 不允许多地登录,强制下线
            SendInfo<Object> sendInfo = new SendInfo<>();
            sendInfo.setCmd(NettyCmdType.FORCE_LOGOUT.getCode());
            sendInfo.setData("您已在其他地方登陆，将被强制下线");
            context.channel().writeAndFlush(sendInfo);
            UserChannelCtxMap.removeContext(userId,platform);
            log.info("异地登录，强制下线,userId:{}", userId);
        }
        // 绑定用户和channel
        UserChannelCtxMap.addContext(userId, platform, ctx);
        // 设置用户id属性
        AttributeKey<String> userIdAttr = AttributeKey.valueOf(ChannelAttrKeys.USER_ID);
        ctx.channel().attr(userIdAttr).set(userId);
        // 设置用户终端类型
        AttributeKey<Integer> terminalAttr = AttributeKey.valueOf(ChannelAttrKeys.TERMINAL_TYPE);
        ctx.channel().attr(terminalAttr).set(platform);
        // 初始化心跳次数
        AttributeKey<Long> heartBeatAttr = AttributeKey.valueOf(ChannelAttrKeys.HEARTBEAT_TIMES);
        ctx.channel().attr(heartBeatAttr).set(0L);
        // 在redis上记录每个user的channelId，15秒没有心跳，则自动过期
        String key = ChatRedisKeys.CHAT_USER_SERVER_ID + userId + ":"+platform;
        redisTemplate.opsForValue().set(key, 1, ChatSystemConstants.ONLINE_TIMEOUT_SECOND, TimeUnit.SECONDS);
        // 响应ws
        SendInfo<Object> sendInfo = new SendInfo<>();
        sendInfo.setCmd(NettyCmdType.CONNECT.getCode());
        sendInfo.setData("连接成功");
        ctx.channel().writeAndFlush(sendInfo);
    }


    private boolean validateToken(Integer platform,String token){
        if(!JwtUtils.isTokenValid(token)){
            return false;
        }
        //如果accessToken没有过期，但是redis中没有，说明其他处的登录挤掉了本次登录
        String userId = JwtUtils.getUserIdFromToken(token);
        String key = String.join(":",ChatRedisKeys.USER_ACCESS_TOKEN,platform.toString(),userId);
        String tokenInRedis = stringRedisTemplate.opsForValue().get(key);
        return tokenInRedis != null&&tokenInRedis.equals(token);
    }

}
