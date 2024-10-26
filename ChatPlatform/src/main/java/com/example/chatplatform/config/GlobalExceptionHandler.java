package com.example.chatplatform.config;

import com.example.chatplatform.entity.CustomException;
import com.example.chatplatform.entity.enums.ResponseEnum;
import com.example.chatplatform.entity.response.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.AccessDeniedException;

/**
 * @author archi
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception e){
        e.printStackTrace();
        return new ResponseEntity<>().error(100,"服务端错误");
    }
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity handleUsernameNotFoundException(UsernameNotFoundException e){
        return new ResponseEntity<>().error(ResponseEnum.USER_NOT_FOUND_ERROR.getCode(),e.getMessage());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity handleValidationException(MethodArgumentNotValidException ex) {
        ex.printStackTrace();
        //获取到设置的错误信息
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return new ResponseEntity<>().error(ResponseEnum.PARAM_FORMAT_ERROR.getCode(), ResponseEnum.PARAM_FORMAT_ERROR.getMessage()+errorMessage);
    }
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity handleAccessDeniedException(AccessDeniedException e){
        return new ResponseEntity<>().error(ResponseEnum.USER_ACCESS_DENIED.getCode(),ResponseEnum.USER_ACCESS_DENIED.getMessage());
    }
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity handleAccessDeniedException(NoResourceFoundException e){
        return new ResponseEntity<>().error(ResponseEnum.RESOURCE_NOT_FOUND_ERROR.getCode(),ResponseEnum.RESOURCE_NOT_FOUND_ERROR.getMessage());
    }
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity parameterMissException(MissingServletRequestParameterException e){
        e.printStackTrace();
        return new ResponseEntity<>().error(ResponseEnum.PARAM_NOT_ENOUGH_ERROR.getCode(),ResponseEnum.PARAM_NOT_ENOUGH_ERROR.getMessage());
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity customException(CustomException e){
        log.info(e.getCode()+e.getMessage());
        return new ResponseEntity<>().error(e.getCode(),e.getMessage());
    }
}
