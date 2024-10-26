package com.example.chatplatform.entity.dto.login;

import jakarta.validation.constraints.*;
import lombok.Data;
import com.example.chatplatform.entity.constants.FormatMessage;
import com.example.chatplatform.entity.constants.RegexPatterns;
import org.hibernate.validator.constraints.Range;

/**
 * @author archi
 */
@Data
public class LoginDTO {

    @Pattern(regexp = RegexPatterns.PASSWORD_REGEX,message = FormatMessage.PWD_FORMAT_ERROR)
    private String password;
    @Range(min = 0,max = 2,message = FormatMessage.LOGIN_PLATFORM_RANGE)
    @NotNull(message = FormatMessage.LOGIN_PLATFORM_NULL)
    private Integer platform;
}
