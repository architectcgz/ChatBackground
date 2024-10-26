package com.example.chatplatform.mapper;

import com.example.chatplatform.entity.po.Group;
import com.example.chatplatform.entity.po.GroupMember;
import com.example.chatplatform.entity.po.GroupRequest;
import com.example.chatplatform.entity.vo.GroupInfoVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

/**
 * @author archi
 */
@Mapper
public interface GroupMapper {

    void createGroup(Group group);

    Group getGroupByUid(String groupId);

    /**
     * 通过userId获取某个user在群中的信息.
     * 如进群时间,成员类型等
     * @param userId
     * @return
     */
    GroupMember getMemberInfoByUserId(String userId);

    void dismissGroup(String groupId);

    Integer isUserExist(String groupId, String userId);

    void addUserToGroup(GroupMember groupMember);

    void addGroupRequest(GroupRequest groupRequest);

    List<GroupRequest> getGroupRequestList(String groupId);

    void reviewRequest(String groupId, String userId, String status, String handlerId);

    Integer isLeader(String groupId,String userId);

    void setAdmin(String groupId, String userId);

    void removeAdmin(String groupId, String userId);

    Group getGroupInfoByUid(String groupId);

    /**
     * 查找用户所在的所有群
     *
     * @param userId
     */

    Set<String> getMyGroups(String userId);

    /**
     * 获取群成员集合
     */
    Set<String> getGroupMemberSet(String groupId);

    /**
     * 判断一个群是否存在
     * @param groupId 群id
     * @return 1:存在 0:不存在
     */
    Integer groupExistsById(String groupId);

    List<GroupInfoVO> searchGroupByUid(String groupUid);

    void setGroupPinStatus(String id,Integer pinned);

    void setGroupMuteStatus(String id,Integer muted);

    void unPinGroup(String id);

    void unMuteGroup(String id);
}
