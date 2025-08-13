package com.example.work.mapper.master;

import com.example.work.dto.DisableDeviceDTO;
import com.example.work.dto.UpdateMerchantRegistryDTO;
import com.example.work.entity.master.AdminUser;
import com.example.work.entity.tenant.DeviceInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface AdminUserMapper {

    @Select("SELECT * FROM admin_user WHERE username = #{username}")
    AdminUser findByUsername(String username);

    @Update("UPDATE admin_user SET password = #{encrypted} WHERE username = #{username}")
    int updatePassword(@Param("username") String username,@Param("encrypted") String encrypted);

    int updateMerchantInfo(@Param("merchant_id") String merchant_id,@Param("merchantInfo") UpdateMerchantRegistryDTO merchantInfo);


    @Delete("DELETE FROM merchant_registry WHERE merchant_id = #{merchantID}")
    void deleteByMerchantId(String merchantID);

    @Update("DROP DATABASE IF EXISTS ${dbName} ")
    void deleteDataBase(String dbName);

    void createTable(String tableName);

    void insertDevice(@Param("tableName") String tableName,@Param("device") DeviceInfo device);

    void updateDevice(@Param("merchantId") String merchantId,@Param("tableName") String tableName,@Param("device") DeviceInfo device);

    @Select("SELECT * FROM ${tableName} ORDER BY id DESC LIMIT 1")
    DeviceInfo getDevices(String tableName);

    @Update("DROP TABLE IF EXISTS ${tableName}")
    void deleteDevice(String tableName);

    void createTable_allDevice();

    @Insert("INSERT INTO all_device (deviceTableName) VALUES (#{tableName})")
    void insertAllDevice(String tableName);

    /**
     * 判断当前库是否存在 all_device 表
     */
    @Select("SELECT COUNT(*) > 0 " +
            "FROM information_schema.tables " +
            "WHERE table_schema = DATABASE() " +
            "AND table_name = 'all_device'")
    boolean checkAllDeviceTableExists();

    // 获取所有deviceName表名
    @Select("SELECT deviceTableName FROM all_device")
    List<String> getAllDeviceNames();

    boolean checkTableExists(String tableName);

    @Select("SELECT field_name FROM user_field_permission " +
            "WHERE username = #{username} AND is_visible = TRUE")
    List<String> getVisibleFieldsByUsername(String username);

    @Delete("DELETE FROM all_device WHERE deviceTableName = #{deviceTableName}")
    void deleteAllDevice(String deviceTableName);

    @Update("UPDATE ${deviceName} SET is_disabled = #{isDisabled} " +
            "WHERE id = (SELECT id FROM (SELECT MAX(id) as id FROM ${deviceName}) as temp)")
    void disableDevice(@Param("deviceName") String deviceName,@Param("isDisabled") Boolean isDisabled);
}
