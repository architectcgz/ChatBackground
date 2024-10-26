package com.example.chatplatform.service.impl;

import com.example.chatplatform.entity.CustomException;
import com.example.chatplatform.entity.enums.ResponseEnum;
import com.example.chatplatform.service.FileService;
import com.example.chatplatform.util.FastDFSClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * @author archi
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {
//    @Value("${file.upload-dir}")
//    private String uploadDir;
//    @Value("${server.url}")
//    private String serverUrl;
//    @Resource
//    private FileMapper fileMapper;
//    private final String[]AVATAR_EXTENSIONS = {
//            "jpg","jpeg","png","bmp"
//    };
//    @Override
//    public String uploadAvatar(MultipartFile file) {
//        if(file.isEmpty()){
//            throw new CustomException(ResponseEnum.FILE_EMPTY_ERROR.getCode(),ResponseEnum.FILE_EMPTY_ERROR.getMessage());
//        }
//        String fileName = file.getOriginalFilename();
//        if(!StringUtils.hasLength(fileName)){
//            throw new CustomException(ResponseEnum.FILE_NAME_EMPTY_ERROR.getCode(), ResponseEnum.FILE_NAME_EMPTY_ERROR.getMessage());
//        }
//        if(fileName.length()> SystemConstants.MAX_FILENAME_LENGTH){
//            throw new CustomException(ResponseEnum.FILE_NAME_LENGTH_ERROR.getCode(), ResponseEnum.FILE_NAME_LENGTH_ERROR.getMessage());
//        }
//
//        String fileExtension = fileName.substring(fileName.indexOf(".")+1);
//        if(!Arrays.asList(AVATAR_EXTENSIONS).contains(fileExtension)){
//            throw new CustomException(ResponseEnum.AVATAR_FORMAT_ERROR.getCode(), ResponseEnum.AVATAR_FORMAT_ERROR.getMessage());
//        }
//        //使用时间戳和随机字符串生成新的文件名
//        String shortUID = GenerateUtils.generateShortUID();
//        String newFileName = shortUID + "." + fileExtension;
//        String filePath = uploadDir+"/"+newFileName;
//        try {
//            // 保存文件到服务器
//            File dest = new File(filePath);
//            file.transferTo(dest);
//
//            // 生成文件的URL
//            String fileUrl = serverUrl + "/" + newFileName;
//
//            // 保存文件URL到数据库
//            FileUpload fileEntity = new FileUpload();
//            fileEntity.setFileName(newFileName);
//            fileEntity.setFileUrl(fileUrl);
//            fileMapper.saveFile(fileEntity);
//
//            // 返回文件URL给前端
//            return fileUrl;
//        }catch (IOException e){
//            e.printStackTrace();
//            throw new CustomException(ResponseEnum.FILE_UPLOAD_ERROR.getCode(), ResponseEnum.FILE_UPLOAD_ERROR.getMessage());
//        }
//
//    }
    @Resource
    private FastDFSClient fastDFSClient;
    @Override
    public String uploadAvatar(MultipartFile file) {
        if(file.isEmpty()){
            throw new CustomException(ResponseEnum.FILE_EMPTY_ERROR.getCode(), ResponseEnum.FILE_EMPTY_ERROR.getMessage());
        }
        try {
            String result = fastDFSClient.uploadMultipartFile(file);
            if(result == null){
                throw new CustomException(ResponseEnum.FILE_UPLOAD_ERROR.getCode(),ResponseEnum.FILE_UPLOAD_ERROR.getMessage());
            }
            return result;
        }catch (Exception e){
            throw new CustomException(ResponseEnum.FILE_UPLOAD_ERROR.getCode(),ResponseEnum.FILE_UPLOAD_ERROR.getMessage());
        }
    }

    @Override
    public String uploadQRCode(MultipartFile file) {
        return null;
    }

    @Override
    public String uploadImage(MultipartFile file) {
        if(file.isEmpty()){
            throw new CustomException(ResponseEnum.FILE_EMPTY_ERROR.getCode(), ResponseEnum.FILE_EMPTY_ERROR.getMessage());
        }
        try {
            String result = fastDFSClient.uploadMultipartFile(file);
            if(result == null){
                throw new CustomException(ResponseEnum.FILE_UPLOAD_ERROR.getCode(),ResponseEnum.FILE_UPLOAD_ERROR.getMessage());
            }
            log.info("文件上传结果为: "+result);
            return result;
        }catch (Exception e){
            throw new CustomException(ResponseEnum.FILE_UPLOAD_ERROR.getCode(),ResponseEnum.FILE_UPLOAD_ERROR.getMessage());
        }
    }

    @Override
    public String uploadVoice(MultipartFile voice) {
        if( voice==null||voice.isEmpty()){
            throw new CustomException(ResponseEnum.FILE_EMPTY_ERROR.getCode(), ResponseEnum.FILE_EMPTY_ERROR.getMessage());
        }
        try {
            String voiceFileName = UUID.randomUUID()+".m4a";
            log.info("随机生成的音频文件名为: "+voiceFileName);
            String result = fastDFSClient.uploadMultipartFile(voice);
            if(result == null){
                throw new CustomException(ResponseEnum.FILE_UPLOAD_ERROR.getCode(),ResponseEnum.FILE_UPLOAD_ERROR.getMessage());
            }
            return result;
        }catch (Exception e){
            throw new CustomException(ResponseEnum.FILE_UPLOAD_ERROR.getCode(),ResponseEnum.FILE_UPLOAD_ERROR.getMessage());
        }
    }

    @Override
    public String uploadFile(MultipartFile file) {
        if(file.isEmpty()){
            throw new CustomException(ResponseEnum.FILE_EMPTY_ERROR.getCode(), ResponseEnum.FILE_EMPTY_ERROR.getMessage());
        }
        try {
            String result = fastDFSClient.uploadMultipartFile(file);
            if(result == null){
                throw new CustomException(ResponseEnum.FILE_UPLOAD_ERROR.getCode(),ResponseEnum.FILE_UPLOAD_ERROR.getMessage());
            }
            return result;
        }catch (Exception e){
            throw new CustomException(ResponseEnum.FILE_UPLOAD_ERROR.getCode(),ResponseEnum.FILE_UPLOAD_ERROR.getMessage());
        }
    }
}
