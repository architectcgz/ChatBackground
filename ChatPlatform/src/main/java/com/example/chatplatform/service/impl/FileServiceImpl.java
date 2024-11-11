package com.example.chatplatform.service.impl;

import com.example.chatplatform.entity.CustomRuntimeException;
import com.example.chatplatform.entity.enums.ResponseEnum;
import com.example.chatplatform.service.FileService;
import com.example.chatplatform.util.FastDFSClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * @author archi
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {
    @Resource
    private FastDFSClient fastDFSClient;
    @Override
    public String uploadAvatar(MultipartFile file) {
        if(file.isEmpty()){
            throw new CustomRuntimeException(ResponseEnum.FILE_EMPTY_ERROR.getCode(), ResponseEnum.FILE_EMPTY_ERROR.getMessage());
        }
        try {
            String result = fastDFSClient.uploadMultipartFile(file);
            if(result == null){
                throw new CustomRuntimeException(ResponseEnum.FILE_UPLOAD_ERROR.getCode(),ResponseEnum.FILE_UPLOAD_ERROR.getMessage());
            }
            return result;
        }catch (Exception e){
            throw new CustomRuntimeException(ResponseEnum.FILE_UPLOAD_ERROR.getCode(),ResponseEnum.FILE_UPLOAD_ERROR.getMessage());
        }
    }

    @Override
    public String uploadQRCode(MultipartFile file) {
        return null;
    }

    @Override
    public String uploadImage(MultipartFile file) {
        if(file.isEmpty()){
            throw new CustomRuntimeException(ResponseEnum.FILE_EMPTY_ERROR.getCode(), ResponseEnum.FILE_EMPTY_ERROR.getMessage());
        }
        try {
            String result = fastDFSClient.uploadMultipartFile(file);
            if(result == null){
                throw new CustomRuntimeException(ResponseEnum.FILE_UPLOAD_ERROR.getCode(),ResponseEnum.FILE_UPLOAD_ERROR.getMessage());
            }
            log.info("文件上传结果为: "+result);
            return result;
        }catch (Exception e){
            throw new CustomRuntimeException(ResponseEnum.FILE_UPLOAD_ERROR.getCode(),ResponseEnum.FILE_UPLOAD_ERROR.getMessage());
        }
    }

    @Override
    public String uploadVoice(MultipartFile voice) {
        if( voice==null||voice.isEmpty()){
            throw new CustomRuntimeException(ResponseEnum.FILE_EMPTY_ERROR.getCode(), ResponseEnum.FILE_EMPTY_ERROR.getMessage());
        }
        try {
            String voiceFileName = UUID.randomUUID()+".m4a";
            log.info("随机生成的音频文件名为: "+voiceFileName);
            String result = fastDFSClient.uploadMultipartFile(voice);
            if(result == null){
                throw new CustomRuntimeException(ResponseEnum.FILE_UPLOAD_ERROR.getCode(),ResponseEnum.FILE_UPLOAD_ERROR.getMessage());
            }
            return result;
        }catch (Exception e){
            throw new CustomRuntimeException(ResponseEnum.FILE_UPLOAD_ERROR.getCode(),ResponseEnum.FILE_UPLOAD_ERROR.getMessage());
        }
    }

    @Override
    public String uploadFile(MultipartFile file) {
        if(file.isEmpty()){
            throw new CustomRuntimeException(ResponseEnum.FILE_EMPTY_ERROR.getCode(), ResponseEnum.FILE_EMPTY_ERROR.getMessage());
        }
        try {
            String result = fastDFSClient.uploadMultipartFile(file);
            if(result == null){
                throw new CustomRuntimeException(ResponseEnum.FILE_UPLOAD_ERROR.getCode(),ResponseEnum.FILE_UPLOAD_ERROR.getMessage());
            }
            return result;
        }catch (Exception e){
            throw new CustomRuntimeException(ResponseEnum.FILE_UPLOAD_ERROR.getCode(),ResponseEnum.FILE_UPLOAD_ERROR.getMessage());
        }
    }
}
