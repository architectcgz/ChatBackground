package com.example.chatplatform.entity.dto.register;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import com.example.chatplatform.entity.constants.FormatMessage;
import com.example.chatplatform.entity.constants.RegexPatterns;

/**
 * @author archi
 */
@Data
public class RegisterDTO {
    @NotBlank(message = FormatMessage.CAPTCHA_EMPTY)
    private String captcha;

    @NotEmpty(message = FormatMessage.NICKNAME_EMPTY)
    @Pattern(regexp = RegexPatterns.USERNAME_REGEX,message = FormatMessage.NICKNAME_FORMAT_ERROR)
    private String nickname;
    @NotEmpty(message = FormatMessage.PWD_EMPTY)
    @Pattern(regexp = RegexPatterns.PASSWORD_REGEX,message = FormatMessage.PWD_FORMAT_ERROR)
    private String password;
}
