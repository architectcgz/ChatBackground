package com.example.chatplatform.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author archi
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomRuntimeException extends RuntimeException implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Integer code;
    private String message;
    public CustomRuntimeException(Integer code, String message){
        this.code = code;
        this.message = message;
    }
}
