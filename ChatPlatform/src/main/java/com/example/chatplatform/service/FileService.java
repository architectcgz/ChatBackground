package com.example.chatplatform.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String uploadAvatar(MultipartFile file);
    String uploadQRCode(MultipartFile file);

    String uploadImage(MultipartFile file);

    String uploadVoice(MultipartFile voice);

    String uploadFile(MultipartFile file);
}
