package com.example.chatplatform.service;

import com.example.chatplatform.entity.po.GroupRequest;
import com.example.chatplatform.entity.vo.GroupInfoVO;
import com.example.chatplatform.entity.vo.MyGroupMemberInfoVO;
import com.example.chatplatform.entity.vo.OtherGroupMemberInfoVO;

import java.util.List;
import java.util.Set;

/**
 * @author archi
 */
public interface GroupService {

    /**
     * 创建group
     * @param groupName
     * @return groupUid
     */
    String createGroup(String groupName);
    void dismissGroup(String groupUid);

    void requestGroup(String groupUid);

    List<GroupRequest> getRequestList(String groupUid, Integer pageNum, Integer pageSize);

    void reviewJoinRequest(String groupUid, String userUid, String operation);

    void addAdminToGroup(String groupUid, String adminUid);

    void removeAdminFromGroup(String groupUid, String adminUid);

    GroupInfoVO getGroupInfoByUid(String groupUid);

    Set<String> getGroupMemberSet(String groupUid);

    List<GroupInfoVO> searchGroupByUid(String groupUid);

    void setGroupPinned(String id, Integer pinned);

    void setGroupMuted(String id, Integer muted);

    MyGroupMemberInfoVO getMyInfoInGroup(String groupUid);

    OtherGroupMemberInfoVO getGroupMemberInfo(String groupUid, String memberId);
}
