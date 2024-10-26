package com.example.chatplatform.entity.po;

import lombok.Data;

import java.util.Date;

/**
 * @author archi
 */
@Data
public class FileUpload {
    String fileName;
    String fileUrl;
    String uploadUserId;
    Date createTime;
}
