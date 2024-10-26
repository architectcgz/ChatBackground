package com.example.chatcommon.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo implements Serializable {
    private static final Long serialVersionUID = 1L;
    private String userId;
    private Integer terminal;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserInfo userInfo = (UserInfo) o;
        return Objects.equals(userId, userInfo.userId) && Objects.equals(terminal, userInfo.terminal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, terminal);
    }
}
