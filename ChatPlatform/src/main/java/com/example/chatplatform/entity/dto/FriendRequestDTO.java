package com.example.chatplatform.entity.dto;

import com.example.chatplatform.entity.constants.FormatMessage;
import com.example.chatplatform.entity.constants.RegexPatterns;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author archi
 */
@Data
public class FriendRequestDTO {
    @Pattern(regexp = RegexPatterns.USER_ID_REGEX,message = FormatMessage.FRIEND_ID_FORMAT_ERROR)
    private String friendId;
    private String requestMessage;
    @Length(max = 50,message = FormatMessage.ALIAS_EXCEED_LENGTH_ERROR)
    private String alias;
}
