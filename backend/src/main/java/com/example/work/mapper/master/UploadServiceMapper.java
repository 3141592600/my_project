package com.example.work.mapper.master;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UploadServiceMapper {

    @Select("SELECT uuid FROM ${tableName} WHERE uuid = #{uuid} ORDER BY create_time DESC LIMIT 1")
    String getLatestUUIDFromTable(String tableName);
}
