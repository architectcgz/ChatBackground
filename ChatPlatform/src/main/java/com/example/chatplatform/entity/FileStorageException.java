package com.example.chatplatform.entity;

import org.apache.tomcat.util.http.fileupload.FileUploadException;

/**
 * @author archi
 */
public class FileStorageException extends RuntimeException{
    private Integer code;
    private String message;
    public FileStorageException(Integer code,String message){
        this.code = code;
        this.message = message;
    }
}
