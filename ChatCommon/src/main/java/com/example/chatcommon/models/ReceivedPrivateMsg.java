package com.example.chatcommon.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author archi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceivedPrivateMsg<T> {

    private String sender;

    private T data;
}
