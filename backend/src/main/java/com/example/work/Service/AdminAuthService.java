package com.example.work.Service;

import com.example.work.dto.*;
import com.example.work.entity.master.MerchantRegistry;
import com.example.work.entity.tenant.DeviceInfo;
import com.example.work.entity.tenant.UserInfo;

import java.util.List;

public interface AdminAuthService {
    // 管理员登录
    String login(AdminLoginRequestDTO dto);
    
    // 获取所有用户信息，分页
    List<UserInfo> getAllUsers( );

    // 通过用户名获取密码
    String getPasswordByUsername(String username);

    // 通过用户名修改密码
    boolean changePassword(String username, String oldPassword, String newPassword);

    // 修改商户信息
    boolean updateMerchantInfo(UpdateMerchantRegistryDTO merchantInfo);
    

    MerchantRegistry findByMerchantName(String merchantName);

    void deleteUserByMerchantId(String merchantID);

    void createTableAndInsert(String tableName, String merchantId, DeviceInfo deviceInfo);

    void editDevice(String tableName, String merchantName, DeviceInfo deviceInfo);

    DeviceInfo getDevices(String merchantId, String tableName);

    void deleteDevice(String merchantId, String tableName);

    void insetDeviceInfo(String tableName, String merchantId, DeviceInfo deviceInfo);

    List<AllMerchantDeviceInfoDTO> getAllDeviceInfo(String merchantName);

    Page<AllMerchantDeviceInfoDTO> getAllCompanyDevices(int pageNum, int pageSize);

    void disableDevice(DisableDeviceDTO disableDeviceDTO);
}
