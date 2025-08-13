package com.example.work.Service;

import com.example.work.dto.*;
import com.example.work.entity.tenant.DeviceInfo;
import com.example.work.entity.tenant.UserInfo;

import java.util.List;
import java.util.Map;

public interface AuthService {
    /**
     * 验证登录，成功返回 JWT Token
     */
    String login(LoginRequestDTO loginRequest);

    /**
     * 根据用户名获取个人信息
     */
    UserInfo getUserInfo(String username);

    /**
     * 更新个人信息（不修改密码）
     */
    boolean updateUserInfo(String username,UpdateUserInfoDTO userInfo);

    /**
     * 修改密码
     */
    boolean changePassword(String username, String oldPassword, String newPassword);

    /**
     * 根据用户名查询原始密码
     */
    String getPasswordByUsername(String username);


    List<AllDeviceDTO> getAllDeviceInfo();


    String addUser(UserInfoDTO userInfo, String merchant_id);

    String setPermissions(PermissionRequest request);

    Map<String, List<Map<String, Object>>> getUserDeviceData(String username);

    DeviceInfo getDeviceInfo(String deviceName);
}