package com.example.chatserver.MsgCmdHandlers;

import com.example.chatcommon.enums.NettyCmdType;
import com.example.chatserver.utils.SpringContextHolder;


/**
 * @author archi
 */
public class CommandHandlerFactory {

    public static CommandHandler getHandler(NettyCmdType cmd) {
        switch (cmd){
            case CONNECT -> {
                return SpringContextHolder.getApplicationContext().getBean(ConnectHandler.class);
            }

            case HEART_BEAT -> {
                return SpringContextHolder.getApplicationContext().getBean(HeartBeatHandler.class);
            }
            case PRIVATE_MESSAGE -> {
                return SpringContextHolder.getApplicationContext().getBean(PrivateMessageHandler.class);
            }
            case GROUP_MESSAGE -> {
                return SpringContextHolder.getApplicationContext().getBean(GroupMessageHandler.class);
            }
            case FRIEND_REQUEST -> {
                return SpringContextHolder.getApplicationContext().getBean(FriendRequestHandler.class);
            }
            case GROUP_REQUEST -> {
                return SpringContextHolder.getApplicationContext().getBean(GroupRequestHandler.class);
            }
            case ACCEPT_FRIEND_REQUEST -> {
                return SpringContextHolder.getApplicationContext().getBean(AcceptFriendReqHandler.class);
            }
            case ACCEPT_GROUP_REQUEST -> {
                return SpringContextHolder.getApplicationContext().getBean(AcceptGroupReqHandler.class);
            }
            case SEND_FILE_REQUEST -> {
                return SpringContextHolder.getApplicationContext().getBean(SendFileRequestHandler.class);
            }
            case ACCEPT_FILE_REQUEST -> {
                return SpringContextHolder.getApplicationContext().getBean(AcceptFileRequestHandler.class);
            }
            case FILE_SLICE -> {
                return SpringContextHolder.getApplicationContext().getBean(FileHandler.class);
            }
            default -> {
                return null;
            }
        }
    }
}
