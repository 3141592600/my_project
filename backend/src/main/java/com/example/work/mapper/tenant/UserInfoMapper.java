package com.example.work.mapper.tenant;

import com.example.work.dto.AllDeviceDTO;
import com.example.work.dto.UpdateUserInfoDTO;
import com.example.work.entity.tenant.UserInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface UserInfoMapper {

    // 根据用户名查询个人信息
    @Select("SELECT * FROM user_info WHERE username = #{username}")
    UserInfo findByUsername(@Param("username") String username);

    // 更新个人信息（不改密码）
    int updateUserInfo(@Param("username") String username,@Param("userInfo") UpdateUserInfoDTO userInfo);

    // 修改密码**************************************
    @Update("UPDATE user_info SET password = #{password} WHERE username = #{username}")
    int updatePassword(@Param("username") String username, @Param("password") String password);

    // 插入用户信息
    @Insert("INSERT INTO user_info (merchant_id, username, password, real_name, phone, email, role, create_time, pay_status) " +
            "VALUES (#{merchant_id}, #{username}, #{password}, #{real_name}, #{phone}, #{email}, #{role}, #{create_time}, #{payStatus})")
    int insert(UserInfo userInfo);

    // 根据用户名查询原始密码
    @Select("SELECT password FROM user_info WHERE username = #{username}")
    String selectPasswordByUsername(String username);

    @Select("SELECT * FROM user_info")
    List<UserInfo> findAllUsers();
    

    void createTable_user_field_permission();


    @Select("SELECT COUNT(*) FROM user_field_permission WHERE table_name = #{table_name} AND username = #{username}")
    int countByTableNameAndUsername(@Param("table_name") String table_name,@Param("username") String username);

    @Insert("INSERT INTO user_field_permission (table_name, username, field_permissions) " +
            "VALUES (#{table_name}, #{username}, #{fieldPermissions})")
    int insertPermissions(@Param("table_name") String table_name,
                           @Param("username") String username,
                           @Param("fieldPermissions") String fieldPermissions);

    @Update("UPDATE user_field_permission " +
            "SET field_permissions = #{fieldPermissions} " +
            "WHERE table_name = #{table_name} AND username = #{username}")
    int updatePermissions(@Param("table_name") String table_name,
                          @Param("username") String username,
                          @Param("fieldPermissions") String fieldPermissions);

    boolean checkTableExists(String tableName);

    @Select("SELECT COUNT(*) > 0 FROM all_device WHERE deviceTableName = #{deviceTableName}")
    boolean findTableNameFromAllDevice(String deviceTableName);

    @Select("SELECT field_permissions FROM user_field_permission WHERE table_name = #{device} AND username = #{username}")
    String getFieldPermissions(@Param("device") String device,@Param("username") String username);

    List<String> getAllFieldNamesFromTable(String tableName);

}