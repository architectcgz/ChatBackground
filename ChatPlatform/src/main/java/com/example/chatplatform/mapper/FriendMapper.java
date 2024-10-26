package com.example.chatplatform.mapper;


import com.example.chatplatform.entity.po.Friend;
import com.example.chatplatform.entity.po.FriendRequest;
import com.example.chatplatform.entity.vo.FriendInfoVO;
import com.example.chatplatform.entity.vo.FriendRequestVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author archi
 */
@Mapper
public interface FriendMapper {
    void insertFriend(String userUid, String friendUid,String alias);
    void insertAcceptedFriend(String userUid,String friendUid,String alias);

    void setFriendStatus(String userUid, String friendUid, Integer status);

    void delete(String userUid, String friendUid);
    Friend getFriend(String userUid, String friendUid);

    /**
     * 设置请求者为user 接受者为friend的这条好友请求的状态为已添加
     * @param userUid
     * @param friendUid
     */
    void resolveFriendRequest(String userUid, String friendUid);
    //找到uid为userUid的friend uid列表
    Set<String> getFriendUidSet(String userUid);
    List<String> getRequestedFriend(String userUid,List<String> friendUidList);
    /**
     * 判断user1是否有user2这条好友记录
     * @param user1 用户的uid
     * @param user2 好友的uid
     * @return
     */
    Integer isFriend(String user1, String user2);
    Integer isBlocked(String user1,String user2);

    void updateAlias(String userUid, String friendUid, String alias);

    void addFriendRequest(String userUid, String friendUid, String alias,String requestMessage);

    /**
     * 拿到对userUid发起好友请求的所有请求者的信息
     * @param userUid 请求接收者id
     * @return
     */
    List<FriendRequestVO> requestFriendInfoList(String userUid);

    FriendRequestVO getRequestFriendInfo(String requestFriendId,String userId);

    /**
     * 判断userUid对friendUid发起的好友请求是否存在
     * @param userUid 发起者的uid
     * @param friendUid 接收者的uid
     * @return
     */
    Integer requestExists(String userUid, String friendUid);

    /**
     * requestUser是否对friend发起好友请求
     * @param requestUserId
     * @param friendId
     * @return
     */
    FriendRequest getFriendRequest(String requestUserId,String friendId);

    Date getUpdateTime(String userId, String friendUid);

    FriendInfoVO getFriendInfo(String userId, String friendUid);
}
