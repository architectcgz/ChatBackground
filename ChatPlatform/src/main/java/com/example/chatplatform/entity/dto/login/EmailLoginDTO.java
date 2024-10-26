package com.example.chatplatform.entity.dto.login;

import com.example.chatplatform.entity.constants.FormatMessage;
import com.example.chatplatform.entity.constants.RegexPatterns;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author archi
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class EmailLoginDTO extends LoginDTO{
    @Pattern(regexp = RegexPatterns.EMAIL_REGEX,message = FormatMessage.EMAIL_FORMAT_ERROR)
    @Email(message = FormatMessage.EMAIL_FORMAT_ERROR)
    private String email;
}
