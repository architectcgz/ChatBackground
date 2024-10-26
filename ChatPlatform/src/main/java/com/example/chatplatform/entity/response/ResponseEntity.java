package com.example.chatplatform.entity.response;

import com.example.chatplatform.entity.constants.StatusMessage;
import lombok.Data;


/**
 * @author archi
 */
@Data
public class ResponseEntity<T>{
    private Integer code;
    private String message;
    private T data;
    public ResponseEntity(T data){
        this.data = data;
    }
    public ResponseEntity(){}

    public ResponseEntity<T> ok(){
        this.code = 200;
        this.message = StatusMessage.SUCCESS;
        return this;
    }
    public ResponseEntity<T> error(int code, String errorMsg){
        this.code = code;
        this.message = errorMsg;
        return this;
    }
}
