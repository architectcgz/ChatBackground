package com.example.chatplatform.entity.dto;

import com.example.chatplatform.entity.constants.FormatMessage;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * @author archi
 */
@Data
public class AliasUpdateDTO {
    @NotEmpty(message = FormatMessage.FRIEND_ID_EMPTY_ERROR)
    private String friendUid;
    @NotEmpty(message = FormatMessage.ALIAS_EMPTY_ERROR)
    private String alias;
}
