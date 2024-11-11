package com.example.chatplatform.controller;


import com.example.chatplatform.entity.response.ResponseEntity;
import com.example.chatplatform.service.FileService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author archi
 */
@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    private FileService fileService;
    @PostMapping("/avatar/upload")
    public ResponseEntity<String> uploadAvatar(@RequestBody MultipartFile file){
        return new ResponseEntity<>(fileService.uploadAvatar(file)).ok();
    }

    @PostMapping("/image/upload")
    public ResponseEntity<String> uploadImage(@RequestBody MultipartFile image){
        return new ResponseEntity<>(fileService.uploadImage(image)).ok();
    }

    @PostMapping("/voice/upload")
    public ResponseEntity<String> uploadVoice(@RequestBody MultipartFile voice){
        return new ResponseEntity<String>(fileService.uploadVoice(voice)).ok();
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestBody MultipartFile file){
        return new ResponseEntity<String>(fileService.uploadFile(file)).ok();
    }
}
