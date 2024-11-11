package com.example.chatplatform.service.impl;

import cn.hutool.core.date.DateTime;
import com.example.chatcommon.enums.FriendRequestStatusEnum;
import com.example.chatcommon.enums.NettyCmdType;
import com.example.chatcommon.models.FriendRequestMessage;
import com.example.chatcommon.models.SendInfo;
import com.example.chatplatform.entity.CustomRuntimeException;
import com.example.chatplatform.entity.constants.RedisKeys;
import com.example.chatplatform.entity.constants.SystemConstants;
import com.example.chatplatform.entity.dto.AliasUpdateDTO;
import com.example.chatplatform.entity.dto.FriendRequestDTO;
import com.example.chatplatform.entity.enums.FriendStatusEnum;
import com.example.chatplatform.entity.enums.ResponseEnum;
import com.example.chatplatform.entity.po.Friend;
import com.example.chatplatform.entity.po.FriendRequest;
import com.example.chatplatform.entity.po.User;
import com.example.chatplatform.entity.vo.FriendInfoVO;
import com.example.chatplatform.entity.vo.FriendRequestVO;
import com.example.chatplatform.mapper.FriendMapper;
import com.example.chatplatform.mapper.UserMapper;
import com.example.chatplatform.service.FriendService;
import com.example.chatplatform.util.RedisUtils;
import com.example.chatplatform.util.RedissonUtil;
import com.example.chatplatform.util.SecurityContextUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author archi
 */
@Slf4j
@Service
public class FriendServiceImpl implements FriendService {
    @Resource
    private FriendMapper friendMapper;
    @Resource
    private UserMapper userMapper;

    @Resource
    private RedissonUtil redissonUtil;
    /*
        请求添加对方为好友
     */
    @Override
    public void requestFriend(FriendRequestDTO friendRequestDTO) {
        //获取当前用户的uid
        User user = SecurityContextUtils.getUserNotNull();
        String userUid = user.getUserId();
        String friendUid = friendRequestDTO.getFriendId();
        Friend userAngle = friendMapper.getFriend(userUid,friendUid);
        Friend friendAngle = friendMapper.getFriend(friendUid,userUid);
        //如果在user视角下看为null说明还没添加过对方,插入好友信息
        if(userAngle==null){
            log.info(friendUid);
            friendMapper.insertFriend(userUid,friendUid,friendRequestDTO.getAlias());
            FriendRequest friendRequest = friendMapper.getFriendRequest(userUid,friendUid);
            log.info(String.valueOf("好友请求是否为空: "+ friendRequest==null));
            if(friendRequest!=null&&!friendRequestExpired(friendRequest)){
                throw new CustomRuntimeException(ResponseEnum.HAS_REQUESTED.getCode(),ResponseEnum.HAS_REQUESTED.getMessage());
            }
            friendMapper.addFriendRequest(userUid,friendUid,friendRequestDTO.getAlias(),friendRequestDTO.getRequestMessage());
            sendFriendRequestMsg(friendRequestDTO, user, userUid, friendUid);
            return;
        }

        Integer userAngleStatus = userAngle.getStatus();
        Integer friendAngleStatus = friendAngle.getStatus();
        log.info("从请求发起者的视角来看: "+ userAngleStatus+ ", 从接收者的视角来看: "+ friendAngleStatus);
        //查看好友状态，是否被对方拉黑且删除，如果被对方拉黑且删除，不能添加好友
        if(FriendStatusEnum.BLOCKED_AND_UNFRIENDED.getCode().equals(friendAngleStatus)){
            log.info("被对方拉黑且删除，不能添加好友");
            throw new CustomRuntimeException(ResponseEnum.CANNOT_ADD_FRIEND.getCode(), ResponseEnum.CANNOT_ADD_FRIEND.getMessage());
        }
        //如果把好友拉黑且删除了，用户可以主动添加对方好友，好友状态调整为Accepted 正常
        if(FriendStatusEnum.BLOCKED_AND_UNFRIENDED.getCode().equals(userAngleStatus)){
            log.info("用户把好友拉黑且删除,好友状态调整为Accepted 正常");
            friendMapper.setFriendStatus(userUid,friendUid, FriendStatusEnum.ACCEPTED.getCode());
            sendFriendRequestMsg(friendRequestDTO, user, userUid, friendUid);
            return;
        }
        //如果仅把对方删除或者是被对方删除 重新发送好友申请
        if(FriendStatusEnum.UNFRIENDED.getCode().equals(userAngleStatus)
                ||FriendStatusEnum.UNFRIENDED.getCode().equals(friendAngleStatus)
            ||FriendStatusEnum.BLOCKED.getCode().equals(userAngleStatus)
                ||FriendStatusEnum.BLOCKED.getCode().equals(friendAngleStatus)
        ){
            log.info("仅把对方拉黑或删除或者是被对方拉黑或删除 重新发送好友申请");
            friendMapper.setFriendStatus(userUid,friendUid,FriendStatusEnum.PENDING.getCode());
            sendFriendRequestMsg(friendRequestDTO, user, userUid, friendUid);
        }
        //其他状态无需添加好友，已经在好友列表中
    }

    private void sendFriendRequestMsg(FriendRequestDTO friendRequestDTO, User user, String userUid, String friendUid) {
        //---使用Redisson 转发好友请求信息的部分---
        log.info("使用Redisson发送好友请求信息");
        FriendRequestMessage friendRequest = new FriendRequestMessage();
        friendRequest.setRequestUserId(userUid);
        friendRequest.setTerminal(user.getTerminal());
        friendRequest.setFriendUid(friendRequestDTO.getFriendId());
        friendRequest.setNickname(user.getNickname());
        friendRequest.setAvatar(user.getAvatar());
        friendRequest.setGender(user.getGender());
        friendRequest.setLocation(user.getLocation());
        friendRequest.setRequestMessage(friendRequestDTO.getRequestMessage());
        friendRequest.setStatus(FriendRequestStatusEnum.UNHANDLED.getCode());
        friendRequest.setCreateTime(DateTime.now().toJdkDate());
        friendRequest.setUpdateTime(DateTime.now().toJdkDate());

        SendInfo sendInfo = new SendInfo<>();
        sendInfo.setCmd(NettyCmdType.FRIEND_REQUEST.getCode());
        sendInfo.setData(friendRequest);
        redissonUtil.sendMessage(sendInfo);
        log.info("自此,发送完成");
        //---使用Redisson 转发好友请求信息的部分---
    }

    /**
     * 好友状态 Pending: 用户发起的好友申请待处理 Accepted:双方已成为好友 Rejected:用户的好友申请被拒绝
     * Blocked：用户拉黑了对方Unfriended: 用户删除了好友关系
     * BlockedAndUnfriended: 用户拉黑且删除了对方
     */
    @Override
    public void blockFriend(String friendUid) {
        //获取当前用户的uid
        User user = SecurityContextUtils.getUserNotNull();
        String userUid = user.getUserId();

        Friend userAngle = friendMapper.getFriend(userUid,friendUid);
        Friend friendAngle = friendMapper.getFriend(friendUid,userUid);
        //userAngle为null 说明user并没有添加friend为好友 不能进行拉黑
        if(userAngle==null){
            log.info("user没有添加对方为好友，不能进行拉黑操作");
            throw new CustomRuntimeException(ResponseEnum.CANNOT_DO_NO_FRIEND.getCode(),ResponseEnum.CANNOT_DO_NO_FRIEND.getMessage());
        }

        Integer userAngleStatus = userAngle.getStatus();
        Integer friendAngleStatus = friendAngle.getStatus();
        //好友状态正常，设置状态为 拉黑好友
        if(FriendStatusEnum.ACCEPTED.getCode().equals(userAngleStatus)){
            friendMapper.setFriendStatus(userUid,friendUid,FriendStatusEnum.BLOCKED.getCode());
        }
        //已经将对方删除,再进行拉黑，状态设置为拉黑且删除
        else if(FriendStatusEnum.UNFRIENDED.getCode().equals(userAngleStatus)){
            friendMapper.setFriendStatus(userUid,friendUid,FriendStatusEnum.BLOCKED_AND_UNFRIENDED.getCode());
        }
        //如果好友向自己发出了好友申请，但是自己不同意，设置成拉黑且删除，免打扰
        else if(FriendStatusEnum.PENDING.getCode().equals(friendAngleStatus)||
            FriendStatusEnum.REJECTED.getCode().equals(friendAngleStatus)
        ){
            friendMapper.setFriendStatus(userUid,friendUid,FriendStatusEnum.BLOCKED_AND_UNFRIENDED.getCode());
        }
    }

    @Override
    public void unblockFriend(String friendUid) {
        //获取当前用户的uid
        User user = SecurityContextUtils.getUserNotNull();
        String userUid = user.getUserId();
        Friend userAngle = friendMapper.getFriend(userUid,friendUid);

        //如果user没有添加对面为好友,那么不能进行取消拉黑操作
        if(userAngle==null){
            log.info("user没有添加对方为好友，不能进行取消拉黑操作");
            throw new CustomRuntimeException(ResponseEnum.CANNOT_DO_NO_FRIEND.getCode(),ResponseEnum.CANNOT_DO_NO_FRIEND.getMessage());
        }
        Integer userAngleStatus = userAngle.getStatus();
        //如果已经是拉黑且删除，设置成删除状态
        if(FriendStatusEnum.BLOCKED_AND_UNFRIENDED.getCode().equals(userAngleStatus)){
            friendMapper.setFriendStatus(userUid,friendUid,FriendStatusEnum.UNFRIENDED.getCode());
        }
        //如果是拉黑状态，设置成正常状态
        else if(FriendStatusEnum.BLOCKED.getCode().equals(userAngleStatus)){
            friendMapper.setFriendStatus(userUid,friendUid,FriendStatusEnum.ACCEPTED.getCode());
        }
        //其他状态无需处理
    }

    @Override
    public void unfriendFriend(String friendUid) {
        //获取当前用户的uid
        User user = SecurityContextUtils.getUserNotNull();
        String userUid = user.getUserId();
        Friend userAngle = friendMapper.getFriend(userUid,friendUid);
        //如果user没有添加对面为好友,那么不能进行删除好友操作
        if(userAngle==null){
            log.info("user没有添加对方为好友，不能进行删除好友操作");
            throw new CustomRuntimeException(ResponseEnum.CANNOT_DO_NO_FRIEND.getCode(),ResponseEnum.CANNOT_DO_NO_FRIEND.getMessage());
        }
        Integer userAngleStatus = userAngle.getStatus();
        //如果是拉黑状态,那么将状态设置为拉黑且删除
        if(userAngleStatus.equals(FriendStatusEnum.BLOCKED.getCode())){
            friendMapper.setFriendStatus(userUid,friendUid,FriendStatusEnum.BLOCKED_AND_UNFRIENDED.getCode());
        }
        //未删除状态则设置成删除
        else if(userAngleStatus.equals(FriendStatusEnum.ACCEPTED.getCode())){
            friendMapper.setFriendStatus(userUid,friendUid,FriendStatusEnum.UNFRIENDED.getCode());
        }
        //其他状态无需处理
        //更新缓存
        //有缓存则更新，无则不更新
        String key = RedisKeys.USER_FRIENDS +userUid;
        if(RedisUtils.hasKey(key)){
            log.info("从redis中移除"+userUid+"的好友: "+friendUid);
            delFriendFromRedis(key,friendUid);
        }

    }

    @Override
    public void acceptFriend(String friendUid,String alias) {
        User user = SecurityContextUtils.getUserNotNull();
        String userUid = user.getUserId();
        log.info("请求发起者: "+ friendUid+"接受者: "+userUid);
        FriendRequest friendRequest = friendMapper.getFriendRequest(friendUid,userUid);
        if(friendRequest==null){
            log.info("未接收到好友请求,无法添加对方为好友");
            throw new CustomRuntimeException(ResponseEnum.NO_FRIEND_REQUEST.getCode(),ResponseEnum.NO_FRIEND_REQUEST.getMessage());
        }
        if(friendRequestExpired(friendRequest)){
            throw new CustomRuntimeException(ResponseEnum.FRIEND_REQUEST_EXPIRED.getCode(),ResponseEnum.FRIEND_REQUEST_EXPIRED.getMessage());
        }

        //将request的status设置为已处理
        friendMapper.resolveFriendRequest(friendUid,userUid);
        //在发起者和接受者的好友列表插入好友信息
        //发起者
        if(isFriend(friendUid,userUid)){
            log.info("发送者"+friendUid+"还未添加接受者"+userUid+"为好友"+",发起者对接受者的备注: "+alias);
            friendMapper.insertAcceptedFriend(friendUid,userUid,alias);
        }else{
            friendMapper.setFriendStatus(friendUid,userUid,FriendStatusEnum.ACCEPTED.getCode());
        }
        //更新缓存
        addFriendToRedis(RedisKeys.USER_FRIENDS +friendUid,userUid);
        //接受者
        if(!isFriend(userUid,friendUid)){
            log.info("接收者"+userUid+"还未添加发送者"+friendUid+"为好友"+",接受者对发起者的备注: "+friendRequest.getAlias());
            friendMapper.insertAcceptedFriend(userUid,friendUid,friendRequest.getAlias());
        }else{
            friendMapper.setFriendStatus(userUid,friendUid,FriendStatusEnum.ACCEPTED.getCode());
        }
        addFriendToRedis(RedisKeys.USER_FRIENDS +userUid,friendUid);
    }

    private static boolean friendRequestExpired(FriendRequest friendRequest) {
        //检查好友请求是否已经过期
        Date createTime = friendRequest.getCreateTime();
        // 获取 createTime 的时间戳（毫秒数）
        long createTimeTimestamp = createTime.getTime();
        // 获取当前时间的时间戳（毫秒数）
        long currentTimestamp = System.currentTimeMillis();
        // 计算7天的毫秒数
        return currentTimestamp - createTimeTimestamp > SystemConstants.SEVEN_DAYS_IN_MILLIS;
    }

    @Override
    public List<String> getRequestListPaginated(Integer pageNum, Integer pageSize) {
        User user = SecurityContextUtils.getUserNotNull();
        String userUid = user.getUserId();

        Set<String> friendUidSet;
        String key = RedisKeys.USER_FRIENDS +userUid;
        //因为要找请求添加user为好友的人的uid列表，所以这里要查找的是friend的uid
        //先查缓存
        friendUidSet = RedisUtils.getSet(key);
        if(friendUidSet==null){
            //缓存中找不到,从数据库中找
            friendUidSet = friendMapper.getFriendUidSet(userUid);
            //将查到的好友列表缓存到redis中,7天
            RedisUtils.storeSet(RedisKeys.USER_FRIENDS +userUid,friendUidSet,SystemConstants.FRIEND_LIST_EXPIRATION);
        }
        if(friendUidSet.size()==0){
            return null;
        }
        PageHelper.startPage(pageNum,pageSize);
        Page<String> requestedFriendUidList = (Page<String>) friendMapper.getRequestedFriend(userUid,friendUidSet.stream().toList());
        return requestedFriendUidList.getResult();
    }

    @Override
    public Set<String> getFriendSetPaginated(Integer pageNum, Integer pageSize) {
        User user = SecurityContextUtils.getUserNotNull();
        String userUid = user.getUserId();
        Set<String> friendUidSet;
        //先查缓存
        String key = RedisKeys.USER_FRIENDS +userUid;
        if(RedisUtils.hasKey(key)){
            friendUidSet = RedisUtils.getZSetPaginated(key,pageNum,pageSize);
        }else{
            //缓存中找不到,从数据库中找
            friendUidSet = friendMapper.getFriendUidSet(userUid);
            log.info(String.valueOf(friendUidSet.size()));
            //将查到的好友列表缓存到redis中,7天
            RedisUtils.storeSet(key,friendUidSet, SystemConstants.FRIEND_LIST_EXPIRATION);
            //拿到分页后的数据
            int maxSize = Math.min(pageNum*pageSize-1,friendUidSet.size());
            friendUidSet = RedisUtils.getZSetPaginated(key,pageNum,maxSize);
        }
        return friendUidSet;
    }

    @Override
    public FriendInfoVO getFriendInfoByUid(String friendUid) {
        User user = SecurityContextUtils.getUserNotNull();
        return friendMapper.getFriendInfo(user.getUserId(),friendUid);
    }

    @Override
    public boolean isFriend(String user1, String user2) {
        Integer isFriend =  friendMapper.isFriend(user2,user1);
        //如果有结果,那么两人是好友
        return isFriend != null;
    }

    @Override
    public boolean isBlocked(String user1, String user2) {
        Integer isBlocked = friendMapper.isBlocked(user2,user1);
        return isBlocked!=null;
    }

    @Override
    public List<FriendInfoVO> searchFriendByUid(String friendUid) {
        return userMapper.searchFriend(friendUid);
    }

    @Override
    public Set<String> getAllFriendIds() {
        User user = SecurityContextUtils.getUserNotNull();
        String userUid = user.getUserId();
        Set<String> friendUidSet;
        //先查缓存
        String key = RedisKeys.USER_FRIENDS +userUid;
        if(RedisUtils.hasKey(key)){
            friendUidSet = RedisUtils.getSet(key);
        }else{
            //缓存中找不到,从数据库中找
            friendUidSet = friendMapper.getFriendUidSet(userUid);
            //将查到的好友列表缓存到redis中,7天
            RedisUtils.storeSet(key,friendUidSet, SystemConstants.FRIEND_LIST_EXPIRATION);
        }
        return friendUidSet;
    }

    @Override
    public List<FriendInfoVO> getFriendInfoList() {
        User user = SecurityContextUtils.getUserNotNull();
        Set<String> uidSet = getAllFriendIds();
        if(uidSet.size()>0){
            return userMapper.getFriendInfoByUidSet(user.getUserId(),uidSet);
        }
        return new ArrayList<>();
    }

    @Override
    public void updateFriendAlias(AliasUpdateDTO aliasUpdateDTO) {
        User user = SecurityContextUtils.getUserNotNull();
        friendMapper.updateAlias(user.getUserId(),aliasUpdateDTO.getFriendUid(),aliasUpdateDTO.getAlias());
    }

    @Override
    public List<FriendRequestVO> getRequestList() {
        User user = SecurityContextUtils.getUserNotNull();
        return friendMapper.requestFriendInfoList(user.getUserId());
    }

    @Override
    public FriendRequestVO getRequestFriendInfoByUid(String friendUid) {
        User user = SecurityContextUtils.getUserNotNull();
        return friendMapper.getRequestFriendInfo(friendUid,user.getUserId());
    }

    @Override
    public Date getUpdateTime(String friendUid) {
        User user = SecurityContextUtils.getUserNotNull();
        return userMapper.getUpdateTime(friendUid);
    }

    private void addFriendToRedis(String key, String value){
        RedisUtils.addToSet(key,value);
        RedisUtils.expire(key,SystemConstants.FRIEND_LIST_EXPIRATION, TimeUnit.SECONDS);
    }
    private void delFriendFromRedis(String key, String value){
        RedisUtils.removeFromSet(key, value);
        RedisUtils.expire(key,SystemConstants.FRIEND_LIST_EXPIRATION,TimeUnit.SECONDS);
    }
}
