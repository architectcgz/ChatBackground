package com.example.chatplatform.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author archi
 */

@Data
public class CustomException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer code;
    private String message;
    public CustomException(Integer code,String message){
        this.code = code;
        this.message = message;
    }
}
