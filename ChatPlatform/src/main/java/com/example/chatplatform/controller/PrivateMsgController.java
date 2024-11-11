package com.example.chatplatform.controller;

import com.example.chatplatform.entity.constants.FormatMessage;
import com.example.chatplatform.entity.enums.ResponseEnum;
import com.example.chatplatform.service.PrivateMsgService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import com.example.chatplatform.entity.dto.MessageDTO;
import com.example.chatplatform.entity.response.ResponseEntity;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

/**
 * @author archi
 */
@RestController
@RequestMapping("/message/private")
public class PrivateMsgController {
    @Resource
    private PrivateMsgService privateMsgService;
    @PostMapping("/send")
    public ResponseEntity sendMessage(@Valid @RequestBody MessageDTO msg){
        return new ResponseEntity<>(privateMsgService.sendMessage(msg)).ok();
    }

    @PostMapping("/recall/{id}")
    public ResponseEntity recallMessage(@PathVariable Long id){
        privateMsgService.recallMessage(id);
        return new ResponseEntity<>().ok();
    }

    @GetMapping("/unreadList")
    public ResponseEntity getUnreadMessageList(@Valid @NotEmpty(message = FormatMessage.FRIEND_ID_EMPTY_ERROR)
                                                   @RequestParam String friendId,
                                               @RequestParam("count")Long count){
        if(count==null){
            //默认10条消息
            count = 10L;
        }
        return new ResponseEntity<>(privateMsgService.getUnreadList(friendId,count)).ok();
    }
}
