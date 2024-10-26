package com.example.chatplatform.controller;

import com.example.chatplatform.entity.dto.MessageDTO;
import com.example.chatplatform.entity.response.ResponseEntity;
import com.example.chatplatform.service.GroupMsgService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @author archi
 */
@RestController
@RequestMapping("/message/group")
public class GroupMsgController {
    @Resource
    private GroupMsgService groupMsgService;
    @PostMapping("/send")
    public ResponseEntity sendGroupMessage(@Valid @RequestBody MessageDTO message){
        return new ResponseEntity<>(groupMsgService.sendMessage(message)).ok();
    }
    @PostMapping("/recall")
    public ResponseEntity recallGroupMessage(@RequestParam Long messageId){
        groupMsgService.recallMessage(messageId);
        return new ResponseEntity<>().ok();
    }
}
