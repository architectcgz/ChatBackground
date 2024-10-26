package com.example.chatplatform.mapper;

import com.example.chatplatform.entity.po.GroupMember;
import com.example.chatplatform.entity.vo.MyGroupMemberInfoVO;
import com.example.chatplatform.entity.vo.OtherGroupMemberInfoVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author archi
 */
@Mapper
public interface GroupMemberMapper {

    GroupMember getMyInfoInGroup(String groupUid, String userId);

    OtherGroupMemberInfoVO getOtherUserInfoInGroup(String groupUid,String userId, String memberId);
}
