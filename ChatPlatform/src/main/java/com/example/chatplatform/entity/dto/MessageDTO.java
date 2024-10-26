package com.example.chatplatform.entity.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import com.example.chatplatform.entity.constants.FormatMessage;
import org.hibernate.validator.constraints.Length;

/**
 * @author archi
 */
@Data
public class MessageDTO {
    @NotNull(message = FormatMessage.RECEIVER_NULL_ERROR)
    private String receiverId;

    @Length(max = 500, message = FormatMessage.MESSAGE_LENGTH_RESTRICT)
    private String content;

    @NotNull(message = FormatMessage.MESSAGE_TYPE_EMPTY_ERROR)
    private Integer messageType;

    private String mediaName;
    private String mediaUrl;
    private Long mediaSize;
}
