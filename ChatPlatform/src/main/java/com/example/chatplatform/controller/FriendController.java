package com.example.chatplatform.controller;

import com.example.chatplatform.entity.dto.AliasUpdateDTO;
import com.example.chatplatform.entity.dto.FriendRequestDTO;
import jakarta.annotation.Resource;
import com.example.chatplatform.entity.response.ResponseEntity;
import com.example.chatplatform.service.FriendService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @author archi
 */
@RestController
@RequestMapping("/friend")
public class FriendController {
    @Resource
    private FriendService friendService;
    //请求添加好友
    @PostMapping("/request")
    public ResponseEntity requestFriend(@RequestBody @Valid FriendRequestDTO friendRequestDTO){
        friendService.requestFriend(friendRequestDTO);
        return new ResponseEntity<>().ok();
    }
    //好友请求列表
    @GetMapping("/request/list")
    public ResponseEntity requestList(
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNum != null && pageSize != null) {
            return new ResponseEntity<>(friendService.getRequestListPaginated(pageNum, pageSize)).ok();
        } else {
            return new ResponseEntity<>(friendService.getRequestList()).ok();
        }
    }

    //请求好友信息
    @GetMapping("/request/info")
    public ResponseEntity getRequestFriendInfo(@RequestParam("friendUid")String friendUid){
        return new ResponseEntity<>(friendService.getRequestFriendInfoByUid(friendUid)).ok();
    }

    //同意添加对方为好友
    @PostMapping("/accept")
    public ResponseEntity acceptFriend(@RequestParam("friendUid") String friendUid,
                                       @RequestParam(value = "alias",required = false)String alias){
        friendService.acceptFriend(friendUid,alias);
        return new ResponseEntity<>().ok();
    }
    //拉黑好友
    @PostMapping("/block")
    public ResponseEntity blockFriend(@RequestParam("friendUid") String friendUid){
        friendService.blockFriend(friendUid);
        return new ResponseEntity<>().ok();
    }
    //取消拉黑好友
    @PostMapping("/unblock")
    public ResponseEntity unblockFriend(@RequestParam("friendUid") String friendUid){
        friendService.unblockFriend(friendUid);
        return new ResponseEntity<>().ok();
    }
    //删除好友
    @PostMapping("/unfriend")
    public ResponseEntity unfriendFriend(@RequestParam("friendUid") String friendUid){
        friendService.unfriendFriend(friendUid);
        return new ResponseEntity<>().ok();
    }
    @GetMapping("/info")
    public ResponseEntity getFriendInfoByUid(@RequestParam("friendUid")String friendUid){
        return new ResponseEntity<>(friendService.getFriendInfoByUid(friendUid)).ok();
    }

    @GetMapping("/search")
    public ResponseEntity searchFriend(@RequestParam("friendUid")String friendUid){
        return new ResponseEntity<>(friendService.searchFriendByUid(friendUid)).ok();
    }

    @GetMapping("/id-list/all")
    public ResponseEntity getAllFriends(){
        return new ResponseEntity<>(friendService.getAllFriendIds()).ok();
    }

    @GetMapping("/info/list")
    public ResponseEntity getFriendInfoList(){
        return new ResponseEntity<>(friendService.getFriendInfoList()).ok();
    }

    @PostMapping("/alias/update")
    public ResponseEntity updateFriendAlias(@RequestBody @Valid AliasUpdateDTO aliasUpdateDTO){
        friendService.updateFriendAlias(aliasUpdateDTO);
        return new ResponseEntity<>().ok();
    }

    @GetMapping("/update_time")
    public ResponseEntity getFriendUpdateTime(@RequestParam String friendId){
        return new ResponseEntity<>(friendService.getUpdateTime(friendId)).ok();
    }
}
