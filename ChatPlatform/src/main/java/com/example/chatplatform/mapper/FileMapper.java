package com.example.chatplatform.mapper;

import com.example.chatplatform.entity.po.FileUpload;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author archi
 */
@Mapper
public interface FileMapper {
    void saveFile(FileUpload file);
}
