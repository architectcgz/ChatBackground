package com.example.chatplatform.entity.po;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 群组成员
 *
 * @author archi
 */
@Data
public class GroupMember implements Serializable {
    /**
     * 群聊的uid
     */
    private String groupId;

    /**
     * 成员的uid
     */
    private String memberId;

    /**
     * 群成员的身份 0:群成员 1:管理员 2:群主
     */
    private Integer memberType;
    /**
     *  群成员对群的备注名称
     */
    private String alias;
    /**
     * 该群成员是否把群置顶
     */
    private Boolean pinGroup;

    /**
     * 该群成员是否把群设置为免打扰
     */
    private Boolean muteGroup;

    /**
     * 群成员在群中的名称
     */
    private String memberAlias;
    /**
     * 加入时间
     */
    private Date joinTime;

    private static final long serialVersionUID = 1L;
}