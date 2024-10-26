package com.example.chatplatform.mapper;

import com.example.chatcommon.po.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper {

    Long saveMessage(Message message);

    Message getMsgById(Long id);

    void updateById(Long id,Message message);
}
