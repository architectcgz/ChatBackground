package com.example.chatplatform.entity.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author archi
 * @author archi
 */
@EqualsAndHashCode(callSuper = true)
@Data
/*
  用户自己之外其他群成员的信息
 */
public class OtherGroupMemberInfoVO extends BaseGroupMemberInfoVO{
    private String memberId;
    private String avatar;
    private String memberAlias;
    private String signature;
    private String location;
    private Integer gender;
    /**
     * 当前用户对此群成员的备注
     */
    private String aliasToHim;
}
