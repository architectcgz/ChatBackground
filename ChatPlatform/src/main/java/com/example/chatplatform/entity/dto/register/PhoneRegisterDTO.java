package com.example.chatplatform.entity.dto.register;

import com.example.chatplatform.entity.constants.FormatMessage;
import com.example.chatplatform.entity.constants.RegexPatterns;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author archi
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PhoneRegisterDTO extends RegisterDTO{
    @Pattern(regexp = RegexPatterns.PHONE_REGEX,message = FormatMessage.PHONE_FORMAT_ERROR)
    private String phone;
}
