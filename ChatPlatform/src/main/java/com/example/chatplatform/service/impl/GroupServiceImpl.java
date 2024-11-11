package com.example.chatplatform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.example.chatplatform.entity.vo.GroupInfoVO;
import com.example.chatplatform.entity.vo.MyGroupMemberInfoVO;
import com.example.chatplatform.entity.vo.OtherGroupMemberInfoVO;
import com.example.chatplatform.mapper.GroupMemberMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import com.example.chatplatform.entity.CustomRuntimeException;
import com.example.chatplatform.entity.enums.*;
import com.example.chatplatform.entity.po.Group;
import com.example.chatplatform.entity.po.GroupMember;
import com.example.chatplatform.entity.po.GroupRequest;
import com.example.chatplatform.entity.po.User;
import com.example.chatplatform.mapper.GroupMapper;
import com.example.chatplatform.service.GroupService;
import com.example.chatplatform.util.GenerateUtils;
import com.example.chatplatform.util.SecurityContextUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author archi
 */
@Slf4j
@Service
public class GroupServiceImpl implements GroupService {
    @Resource
    private GroupMapper groupMapper;
    @Resource
    private GroupMemberMapper groupMemberMapper;

    @Override
    public String createGroup(String groupName) {
        User user = SecurityContextUtils.getUserNotNull();
        String userUid = user.getUserId();
        Group group = new Group();
        String groupId = GenerateUtils.generateGroupUid();
        group.setGroupId(groupId);
        group.setGroupName(groupName);
        group.setLeaderId(userUid);
        group.setStatus(GroupStatusEnum.NORMAL.getStatus());
        groupMapper.createGroup(group);
        return groupId;
    }

    @Override
    public void dismissGroup(String groupUid) {
        User user = SecurityContextUtils.getUserNotNull();
        String userUid = user.getUserId();
        //先检查群是否存在
        Group group = groupMapper.getGroupByUid(groupUid);
        if(group==null){
            throw new CustomRuntimeException(ResponseEnum.GROUP_NOT_EXISTS.getCode(),ResponseEnum.GROUP_NOT_EXISTS.getMessage());
        }
        //检查用户是否为群主，否则不能解散群聊
        if(!userUid.equals(group.getLeaderId())){
            throw new CustomRuntimeException(ResponseEnum.CANNOT_DISMISS_GROUP.getCode(), ResponseEnum.CANNOT_DISMISS_GROUP.getMessage());
        }
        groupMapper.dismissGroup(groupUid);
    }

    @Override
    public void requestGroup(String groupUid) {
        User user = SecurityContextUtils.getUserNotNull();
        String userUid = user.getUserId();
        //先检查当前用户是否已经在群聊中
        Integer isUserExist = groupMapper.isUserExist(userUid,groupUid);
        //已经进去无法重复加群
        if(isUserExist!=null){
            throw new CustomRuntimeException(ResponseEnum.ALREADY_IN_GROUP.getCode(),ResponseEnum.ALREADY_IN_GROUP.getMessage());
        }
        Group group = groupMapper.getGroupByUid(groupUid);
        if(group==null){//群不存在,不允许加群
            throw new CustomRuntimeException(ResponseEnum.GROUP_NOT_EXISTS.getCode(), ResponseEnum.GROUP_NOT_EXISTS.getMessage());
        }
        //如果user是群主，直接加进群
        if(userUid.equals(group.getLeaderId())){
            addUserToGroup(groupUid,userUid,GroupMemberTypeEnum.LEADER);
            return;
        }
        //查看进群是否需要审核，如果需要，则放到审核表中，否则直接进群
        //直接加群，不要审核
        if(group.getJoinType().equals(GroupJoinTypeEnum.DIRECT.getGroupJoinType())){
            addUserToGroup(groupUid,userUid,GroupMemberTypeEnum.MEMBER);
            return;
        }else{//需要审核,放到审核列表中
            addUserToRequestList(groupUid,userUid);
        }
    }

    @Override
    public List<GroupRequest> getRequestList(String groupUid, Integer pageNum, Integer pageSize) {
        if(!isGroupExist(groupUid)){//检查群是否存在
            throw new CustomRuntimeException(ResponseEnum.GROUP_NOT_EXISTS.getCode(), ResponseEnum.GROUP_NOT_EXISTS.getMessage());
        }
        User user = SecurityContextUtils.getUserNotNull();
        String userUid = user.getUserId();
        //检查请求用户是否为群主或者管理员 只有这两种身份能够查看请求列表
        GroupMember groupMember = groupMapper.getMemberInfoByUserId(userUid);
        if(!isAdminOrLeader(groupMember)){
            throw new CustomRuntimeException(ResponseEnum.CANNOT_GET_GROUP_REQUEST_LIST.getCode(),ResponseEnum.CANNOT_GET_GROUP_REQUEST_LIST.getMessage());
        }
        PageHelper.startPage(pageNum,pageSize);
        Page<GroupRequest> groupRequestList = (Page<GroupRequest>) groupMapper.getGroupRequestList(groupUid);
        return groupRequestList.getResult();
    }

    @Override
    public void reviewJoinRequest(String groupUid, String requestUserUid, String operation) {
        if(!isGroupExist(groupUid)){//检查群是否存在
            throw new CustomRuntimeException(ResponseEnum.GROUP_NOT_EXISTS.getCode(), ResponseEnum.GROUP_NOT_EXISTS.getMessage());
        }
        //如果操作不是Approved或 Rejected中的一种，那么操作非法
        if(!(operation.equals(GroupRequestStatusEnum.APPROVED.getStatus())||
                operation.equals(GroupRequestStatusEnum.REJECTED.getStatus()))){
            throw new CustomRuntimeException(ResponseEnum.OPERATION_NOT_VALID.getCode(),ResponseEnum.OPERATION_NOT_VALID.getMessage());
        }
        User user = SecurityContextUtils.getUserNotNull();
        String userUid = user.getUserId();
        //检查请求用户是否为群主或者管理员 只有这两种身份能够查看请求列表
        GroupMember groupMember = groupMapper.getMemberInfoByUserId(userUid);
        if(!isAdminOrLeader(groupMember)){
            throw new CustomRuntimeException(ResponseEnum.CANNOT_GET_GROUP_REQUEST_LIST.getCode(),ResponseEnum.CANNOT_GET_GROUP_REQUEST_LIST.getMessage());
        }
        //将请求状态设置成新的状态
        if(operation.equals(GroupRequestStatusEnum.APPROVED.getStatus())){
            groupMapper.reviewRequest(groupUid,requestUserUid,operation,userUid);
        }else if(operation.equals(GroupRequestStatusEnum.REJECTED.getStatus())){
            groupMapper.reviewRequest(groupUid,requestUserUid,operation,userUid);
        }
    }

    @Override
    public void addAdminToGroup(String groupUid, String userToSetId) {
        if(!isGroupExist(groupUid)){//检查群是否存在
            throw new CustomRuntimeException(ResponseEnum.GROUP_NOT_EXISTS.getCode(), ResponseEnum.GROUP_NOT_EXISTS.getMessage());
        }
        User user = SecurityContextUtils.getUserNotNull();
        String userUid = user.getUserId();
        //先判断当前用户是否为群聊的群主，只有群主能添加管理员
        Integer isLeader = groupMapper.isLeader(groupUid,userUid);
        //不是群主,无权添加管理员
        if(isLeader==null){
            throw new CustomRuntimeException(ResponseEnum.CANNOT_ADD_ADMIN.getCode(),ResponseEnum.CANNOT_ADD_ADMIN.getMessage());
        }
        //检查要设置成管理员的用户是否在群聊中
        Integer isUserExist = groupMapper.isUserExist(groupUid,userToSetId);
        if(isUserExist==null){
            throw new CustomRuntimeException(ResponseEnum.USER_NOT_IN_GROUP.getCode(),ResponseEnum.USER_NOT_IN_GROUP.getMessage());
        }
        groupMapper.setAdmin(groupUid,userToSetId);
    }

    @Override
    public void removeAdminFromGroup(String groupUid, String userToRemoveId) {
        if(!isGroupExist(groupUid)){//检查群是否存在
            throw new CustomRuntimeException(ResponseEnum.GROUP_NOT_EXISTS.getCode(), ResponseEnum.GROUP_NOT_EXISTS.getMessage());
        }
        User user = SecurityContextUtils.getUserNotNull();
        String userUid = user.getUserId();
        //先判断当前用户是否为群聊的群主，只有群主能添加/删除管理员
        Integer isLeader = groupMapper.isLeader(groupUid,userUid);
        //不是群主,无权删除管理员
        if(isLeader==null){
            throw new CustomRuntimeException(ResponseEnum.CANNOT_DELETE_ADMIN.getCode(),ResponseEnum.CANNOT_DELETE_ADMIN.getMessage());
        }
        //检查要设置成管理员的用户是否在群聊中
        Integer isUserExist = groupMapper.isUserExist(groupUid,userToRemoveId);
        if(isUserExist==null){
            throw new CustomRuntimeException(ResponseEnum.USER_NOT_IN_GROUP.getCode(),ResponseEnum.USER_NOT_IN_GROUP.getMessage());
        }
        groupMapper.removeAdmin(groupUid,userToRemoveId);
    }

    @Override
    public GroupInfoVO getGroupInfoByUid(String groupUid) {
        Group group = groupMapper.getGroupInfoByUid(groupUid);
        return BeanUtil.copyProperties(group,GroupInfoVO.class);
    }

    @Override
    public Set<String> getGroupMemberSet(String groupUid) {
        if(!isGroupExist(groupUid)){
            return Collections.emptySet();
        }
        return groupMapper.getGroupMemberSet(groupUid);
    }

    /**
     * 模糊搜索群
     * @param groupUid
     * @return
     */
    @Override
    public List<GroupInfoVO> searchGroupByUid(String groupUid) {
        return groupMapper.searchGroupByUid(groupUid);
    }

    @Override
    public void setGroupPinned(String id, Integer pinned) {
        groupMapper.setGroupPinStatus(id,pinned);
    }

    @Override
    public void setGroupMuted(String id, Integer muted) {
        groupMapper.setGroupMuteStatus(id,muted);
    }

    @Override
    public MyGroupMemberInfoVO getMyInfoInGroup(String groupUid) {
        User user = SecurityContextUtils.getUserNotNull();
        GroupMember groupMember = groupMemberMapper.getMyInfoInGroup(groupUid,user.getUserId());
        return BeanUtil.copyProperties(groupMember,MyGroupMemberInfoVO.class);
    }

    @Override
    public OtherGroupMemberInfoVO getGroupMemberInfo(String groupUid, String memberId) {
        User user = SecurityContextUtils.getUserNotNull();
        return groupMemberMapper.getOtherUserInfoInGroup(groupUid,user.getUserId(),memberId);
    }

    /**
     * 判断一个群是否存在
     * @param groupUid 群id
     * @return true 存在,false不存在
     */
    private boolean isGroupExist(String groupUid){
        return groupMapper.groupExistsById(groupUid)!=null;
    }


    private boolean isAdminOrLeader(GroupMember groupMember){
        return groupMember.getMemberType().equals(GroupMemberTypeEnum.LEADER.getMemberType())
                || groupMember.getMemberType().equals(GroupMemberTypeEnum.ADMIN.getMemberType());
    }
    private void addUserToGroup(String groupId,String userUid,GroupMemberTypeEnum type){
        GroupMember groupMember = new GroupMember();
        groupMember.setGroupId(groupId);
        groupMember.setMemberId(userUid);
        groupMember.setMemberType(GroupMemberTypeEnum.LEADER.getMemberType());
        groupMember.setJoinTime(DateTime.now());
        groupMapper.addUserToGroup(groupMember);
    }
    private void addUserToRequestList(String groupId,String userUid){
        GroupRequest groupRequest = new GroupRequest();
        groupRequest.setGroupId(groupId);
        groupRequest.setUserId(userUid);
        groupRequest.setRequestTime(DateTime.now());
        groupRequest.setStatus(GroupRequestStatusEnum.PENDING.getStatus());
        groupMapper.addGroupRequest(groupRequest);
    }


}
