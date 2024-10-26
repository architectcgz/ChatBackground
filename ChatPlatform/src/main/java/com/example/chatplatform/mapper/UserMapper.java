package com.example.chatplatform.mapper;


import com.example.chatplatform.entity.po.User;
import com.example.chatplatform.entity.vo.FriendInfoVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author archi
 */
@Mapper
public interface UserMapper {
    void register(User user);
    User findByEmail(String email);
    User getUserInfoByUid(String userId);

    void updateByUserId(String userId, User user);

    User findByPhone(String phone);

    List<FriendInfoVO> searchFriend(String friendId);

    List<FriendInfoVO> getFriendInfoByUidSet(String userId,Set<String> uidSet);

    Date getUpdateTime(String friendUid);
}
