package com.example.chatplatform.service;



import com.example.chatplatform.entity.dto.AliasUpdateDTO;
import com.example.chatplatform.entity.dto.FriendRequestDTO;
import com.example.chatplatform.entity.po.FriendRequest;
import com.example.chatplatform.entity.vo.FriendInfoVO;
import com.example.chatplatform.entity.vo.FriendRequestVO;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author archi
 */
public interface FriendService {
    void requestFriend(FriendRequestDTO friendRequestDTO);
    void blockFriend(String friendUid);

    void unblockFriend(String friendUid);

    void unfriendFriend(String friendUid);

    void acceptFriend(String friendUid,String alias);

    List<String> getRequestListPaginated(Integer pageNum, Integer pageSize);

    Set<String> getFriendSetPaginated(Integer pageNum, Integer pageSize);

    FriendInfoVO getFriendInfoByUid(String friendUid);

    /**
     * user1 是否为user2的好友
     * @param user1
     * @param user2
     * @return true-> 是, false->不是
     */
    boolean isFriend(String user1, String user2);

    /**
     * user1 是否被user2拉黑
     * @param user1
     * @param user2
     * @return true-> 是, false->不是
     */
    boolean isBlocked(String user1, String user2);

    List<FriendInfoVO> searchFriendByUid(String friendUid);

    Set<String> getAllFriendIds();

    List<FriendInfoVO> getFriendInfoList();

    void updateFriendAlias(AliasUpdateDTO aliasUpdateDTO);

    List<FriendRequestVO> getRequestList();

    FriendRequestVO getRequestFriendInfoByUid(String friendUid);

    Date getUpdateTime(String friendId);
}
