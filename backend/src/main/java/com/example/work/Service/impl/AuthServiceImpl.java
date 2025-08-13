package com.example.work.Service.impl;

import com.example.work.Service.AuthService;
import com.example.work.dto.*;
import com.example.work.entity.master.MerchantRegistry;
import com.example.work.entity.tenant.DeviceInfo;
import com.example.work.entity.tenant.UserInfo;
import com.example.work.mapper.master.AdminUserMapper;
import com.example.work.mapper.master.MerchantMapper;
import com.example.work.mapper.tenant.UserInfoMapper;
import com.example.work.util.JwtUtil;
import com.example.work.util.PasswordUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MerchantMapper merchantMapper;
    private final UserInfoMapper userInfoMapper;
    private final AdminUserMapper adminUserMapper;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;  // 注入 JwtUtil

    @Override
    public String login(LoginRequestDTO loginRequest) {
        MerchantRegistry merchant = merchantMapper.findByUsername(loginRequest.getUsername());
//        System.out.println("merchant=============="+merchant);
        if (merchant == null) {
//            throw new RuntimeException("用户名不存在");
            return "用户名不存在";
        }
        if (!PasswordUtil.matches(loginRequest.getPassword(), merchant.getPassword())) {
//            throw new RuntimeException("密码错误");
            return "密码错误";
        }
        return jwtUtil.generateMerchantToken(merchant.getMerchant_id(), merchant.getUsername());
    }

    @Override
    public UserInfo getUserInfo(String username) {
        return userInfoMapper.findByUsername(username);
    }

    @Override
    public boolean updateUserInfo(String username,UpdateUserInfoDTO userInfo) {
        return userInfoMapper.updateUserInfo(username,userInfo) > 0;
    }

    @Override
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        UserInfo dbUser = userInfoMapper.findByUsername(username);
        if (dbUser == null ) {
            throw new RuntimeException("用户不存在");
        }
        // 校验旧密码
        if (!PasswordUtil.matches(oldPassword, userInfoMapper.selectPasswordByUsername(username))) {
            throw new RuntimeException("原密码错误");
        }
        // 更新为新密码
        String encrypted = PasswordUtil.encrypt(newPassword);
        return userInfoMapper.updatePassword(username, encrypted) > 0;
    }

    @Override
    public String getPasswordByUsername(String username) {
        return userInfoMapper.selectPasswordByUsername(username);
    }


    @Override
    public List<AllDeviceDTO> getAllDeviceInfo() {
        try {
            List<String> deviceNames = adminUserMapper.getAllDeviceNames();
            List<AllDeviceDTO> result = new ArrayList<>();
            for (String deviceName : deviceNames) {
                DeviceInfo data = null;
                try {
                    data = adminUserMapper.getDevices(deviceName);
                } catch (Exception e) {
                    // 表不存在或查询失败可做日志或忽略
                    continue;
                }
                if (data != null ) {
                    AllDeviceDTO dmd = new AllDeviceDTO();
                    dmd.setDeviceName(deviceName);
                    dmd.setData(data);
                    result.add(dmd);
                }
            }
            return result;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public String addUser(UserInfoDTO dto, String merchantId) {
        UserInfo userInfo = new UserInfo();
        userInfo.setMerchant_id(merchantId);
        userInfo.setUsername(dto.getUsername());
        userInfo.setPassword(PasswordUtil.encrypt(dto.getPassword()));
        userInfo.setReal_name(dto.getReal_name());
        userInfo.setPhone(dto.getPhone());
        userInfo.setEmail(dto.getEmail());
        userInfo.setRole("二级用户");
        userInfo.setCreate_time(LocalDateTime.now());
        // 查询库中是否存在user_field_permission表，不存在就调用userInfoMapper.createTable_user_field_permission();
        if (!userInfoMapper.checkTableExists("user_field_permission")) {
            userInfoMapper.createTable_user_field_permission();
            System.out.println("创建user_field_permission表成功");
        }

        // 查询该username是否已经被使用
        if (userInfoMapper.findByUsername(dto.getUsername()) != null) {
            return "用户名已存在";
        }

        int rows = userInfoMapper.insert(userInfo);
        if (rows <= 0) {
            return "添加用户失败";
        }

        // 新增用户后，自动给该用户设置所有设备表的全部字段权限为true
        List<String> deviceTables = adminUserMapper.getAllDeviceNames();
        for (String tableName : deviceTables) {
            // 查询该设备表的所有字段名，假设你有一个方法能获取字段名列表
            List<String> fieldNames = userInfoMapper.getAllFieldNamesFromTable(tableName);
            if (fieldNames == null || fieldNames.isEmpty()) {
                continue;
            }
            // 构造全部字段权限全为true的Map
            Map<String, Boolean> permissionMap = new HashMap<>();
            for (String field : fieldNames) {
                permissionMap.put(field, true);
            }

            // 转成JSON字符串
            String jsonPermissions;
            try {
                jsonPermissions = new ObjectMapper().writeValueAsString(permissionMap);
            } catch (JsonProcessingException e) {
                jsonPermissions = "{}"; // 出错则空权限
            }

            // 插入权限记录
            int rows2 =userInfoMapper.insertPermissions(tableName, dto.getUsername(), jsonPermissions);
            if (rows2 <= 0) {
                return "添加用户权限失败";
            }
        }
        return "添加用户成功";
    }

    @Override
    public String setPermissions(PermissionRequest request) {
        // 查询库中是否存在user_field_permission表，不存在就调用userInfoMapper.createTable_user_field_permission();
        if (!userInfoMapper.checkTableExists("user_field_permission")) {
            userInfoMapper.createTable_user_field_permission();
            System.out.println("创建user_field_permission表成功");
        }
        String jsonPermissions;
        try {
            jsonPermissions = objectMapper.writeValueAsString(request.getFieldPermissions());
        } catch (JsonProcessingException e) {
            return ("字段权限转换JSON失败");
        }
        if (!userInfoMapper.findTableNameFromAllDevice(request.getTable_name())){
            return "该设备不存在";
        }
        int count = userInfoMapper.countByTableNameAndUsername(request.getTable_name(), request.getUsername());
        if (count == 0) {
            userInfoMapper.insertPermissions(request.getTable_name(), request.getUsername(), jsonPermissions);
        } else {
            userInfoMapper.updatePermissions(request.getTable_name(), request.getUsername(), jsonPermissions);
        }
        return "权限设置成功";
    }

    @Override
    public Map<String, List<Map<String, Object>>> getUserDeviceData(String username) {
        Map<String, List<Map<String, Object>>> result = new HashMap<>();

        // 1. 查询所有设备表名
        List<String> tableNames = adminUserMapper.getAllDeviceNames();

        for (String tableName : tableNames) {
            // 2. 查询用户该表的字段权限
            String jsonPermissions = userInfoMapper.getFieldPermissions(username, tableName);
            if (jsonPermissions == null || jsonPermissions.isEmpty()) {
                continue; // 没权限配置，跳过该表
            }

            // 3. 解析 JSON 获取允许的字段集合
            Map<String, Boolean> permissionsMap;
            try {
                permissionsMap = new ObjectMapper().readValue(jsonPermissions, new TypeReference<Map<String, Boolean>>() {});
            } catch (JsonProcessingException e) {
                // 解析失败，跳过该表
                continue;
            }

            List<String> allowedFields = permissionsMap.entrySet().stream()
                    .filter(Map.Entry::getValue) // value为true的字段
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            if (allowedFields.isEmpty()) {
                continue; // 没有可读字段，跳过
            }

            // 4. 拼接查询字段SQL
            String fields = String.join(", ", allowedFields);

            // 5. 查询设备表数据，只查允许字段
            String sql = "SELECT " + fields + " FROM " + tableName + " LIMIT 100"; // 限制条数防爆表
            List<Map<String, Object>> data = jdbcTemplate.queryForList(sql);

            result.put(tableName, data);
        }

        return result;
    }

    @Override
    public DeviceInfo getDeviceInfo(String deviceName) {
        return adminUserMapper.getDevices(deviceName);
    }

}
