package com.example.chatplatform.controller;

import com.example.chatplatform.entity.po.User;
import com.example.chatplatform.util.SecurityContextUtils;
import jakarta.annotation.Resource;
import com.example.chatplatform.entity.response.ResponseEntity;
import com.example.chatplatform.service.GroupService;
import org.springframework.web.bind.annotation.*;

/**
 * @author archi
 */
@RestController
@RequestMapping("/group")
public class GroupController {
    @Resource
    private GroupService groupService;
    @PostMapping("/create")
    public ResponseEntity createGroup(@RequestParam("groupName") String groupName){
        return new ResponseEntity<>(groupService.createGroup(groupName)).ok();
    }
    //解散群聊
    @PostMapping("/dismiss")
    public ResponseEntity dismissGroup(@RequestParam("groupUid") String groupUid){
        groupService.dismissGroup(groupUid);
        return new ResponseEntity<>().ok();
    }

    /**
     * 添加新的管理员
     * @param groupUid 操作的群聊的uid
     * @param adminUid 要添加成管理员的用户uid
     * @return
     */
    @PostMapping("/admin/add")
    public ResponseEntity addAdminToGroup(@RequestParam("groupUid") String groupUid,
                                          @RequestParam("adminUid")String adminUid){
        groupService.addAdminToGroup(groupUid,adminUid);
        return new ResponseEntity<>().ok();
    }
    /**
     * 撤销管理员
     * @param groupUid 操作的群聊的uid
     * @param adminUid 要撤销管理员的用户uid
     * @return
     */
    @PostMapping("/admin/remove")
    public ResponseEntity removeAdminFromGroup(@RequestParam("groupUid") String groupUid,
                                               @RequestParam("adminUid")String adminUid){
        groupService.removeAdminFromGroup(groupUid,adminUid);
        return new ResponseEntity<>().ok();
    }


    // 请求加入群聊
    @PostMapping("/request")
    public ResponseEntity requestGroup(@RequestParam("groupUid") String groupUid){
        groupService.requestGroup(groupUid);
        return new ResponseEntity<>().ok();
    }
    //加群请求列表
    @GetMapping("/request/list/{groupUid}")
    public ResponseEntity requestList(@PathVariable("groupUid") String groupUid,
                                      @RequestParam("pageNum") Integer pageNum,
                                      @RequestParam("pageSize") Integer pageSize){
        return new ResponseEntity<>(groupService.getRequestList(groupUid,pageNum,pageSize)).ok();
    }

    /**
     * 管理员或群主审核申请加群的user
     * @param groupUid 要加的群的uid
     * @param userUid 申请加群的user的uid
     * @return
     */
    @PostMapping("/request/review")
    public ResponseEntity reviewJoinRequest(@RequestParam("groupUid") String groupUid,
                                            @RequestParam("userUid") String userUid,
                                            @RequestParam("operation")String operation){
        groupService.reviewJoinRequest(groupUid,userUid,operation);
        return new ResponseEntity<>().ok();
    }

    /**
     * 通过groupUid 查找群信息
     * @param groupUid
     * @return 查找到的群的信息
     */
    @GetMapping("/info")
    public ResponseEntity getGroupInfoByUid(@RequestParam("groupUid")String groupUid){
        return new ResponseEntity<>(groupService.getGroupInfoByUid(groupUid)).ok();
    }
    @GetMapping("/member/info/me")
    public ResponseEntity getMyInfoInGroup(@RequestParam("groupUid")String groupUid){
        return new ResponseEntity<>(groupService.getMyInfoInGroup(groupUid));
    }
    @GetMapping("/{groupUid}/member/info")
    public ResponseEntity getGroupMemberInfo(@PathVariable("groupUid")String groupUid,@RequestParam("memberId")String memberId){
        return new ResponseEntity<>(groupService.getGroupMemberInfo(groupUid,memberId));
    }

    @GetMapping("/search")
    public ResponseEntity searchGroupByUid(@RequestParam("groupUid")String groupUid){
        return new ResponseEntity<>(groupService.searchGroupByUid(groupUid)).ok();
    }

    /**
     * 通过groupUid查群成员集合
     * @param groupUid
     * @return
     */
    @GetMapping("/members")
    public ResponseEntity getGroupMembers(@RequestParam("groupUid")String groupUid){
        return new ResponseEntity<>(groupService.getGroupMemberSet(groupUid)).ok();
    }

    @PostMapping("/pin")
    public ResponseEntity pinGroup(){
        User user = SecurityContextUtils.getUserNotNull();
        groupService.setGroupPinned(user.getUserId(), 1);
        return new ResponseEntity<>().ok();
    }
    @PostMapping("/unpin")
    public ResponseEntity unPinGroup(){
        User user = SecurityContextUtils.getUserNotNull();
        groupService.setGroupPinned(user.getUserId(),0);
        return new ResponseEntity<>().ok();
    }
    @PostMapping("/mute")
    public ResponseEntity muteGroup(){
        User user = SecurityContextUtils.getUserNotNull();
        groupService.setGroupMuted(user.getUserId(),1 );
        return new ResponseEntity<>().ok();
    }
    @PostMapping("/unmute")
    public ResponseEntity unMuteGroup(){
        User user = SecurityContextUtils.getUserNotNull();
        groupService.setGroupMuted(user.getUserId(),0 );
        return new ResponseEntity<>().ok();
    }
}
