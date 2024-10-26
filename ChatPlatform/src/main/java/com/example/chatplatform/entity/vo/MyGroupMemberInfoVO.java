package com.example.chatplatform.entity.vo;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author archi
 */
@Data
@EqualsAndHashCode(callSuper = true)
/*
    用户自己在群中的信息
 */
public class MyGroupMemberInfoVO extends BaseGroupMemberInfoVO{
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

}
