package com.example.work.Service.impl;


import com.example.work.Service.AdminAuthService;
import com.example.work.config.DynamicDataSource;
import com.example.work.dto.*;
import com.example.work.entity.master.AdminUser;
import com.example.work.entity.master.MerchantRegistry;
import com.example.work.entity.tenant.DeviceInfo;
import com.example.work.entity.tenant.UserInfo;
import com.example.work.mapper.master.AdminUserMapper;
import com.example.work.mapper.master.MerchantMapper;
import com.example.work.mapper.tenant.UserInfoMapper;
import com.example.work.util.DataSourceContextHolder;
import com.example.work.util.JwtUtil;
import com.example.work.util.PasswordUtil;
import com.example.work.util.TenantDataSourceProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService {

    private final AdminUserMapper adminUserMapper;
    private final MerchantMapper merchantMapper;
    private final UserInfoMapper userInfoMapper;
    private final DynamicDataSource dynamicDataSource;
    private final JwtUtil jwtUtil;
    private final JdbcTemplate jdbcTemplate;

    // 这里写死的秘钥，实际建议从配置或数据库加载
    private static final String ADMIN_JWT_SECRET = "admin_secret_key_1234567890";

    @Override
    public String login(AdminLoginRequestDTO dto) {
        AdminUser admin = adminUserMapper.findByUsername(dto.getUsername());
        System.out.println("admin=============="+admin);
        if (admin == null) {
//            throw new RuntimeException("管理员账号不存在");
            return "管理员账号不存在";
        }
        if (!PasswordUtil.matches(dto.getPassword(),admin.getPassword())) {
//            throw new RuntimeException("密码错误");
            return "密码错误";
        }
        // 生成带角色信息的token
        return jwtUtil.generateAdminToken(admin.getUsername(), admin.getRole());
    }

    /**
     * 管理员获取所有用户
     */
    public List<UserInfo> getAllUsers() {

        List<UserInfo> allUsers = new ArrayList<>();

        // 1. 切到 master_db
        DataSourceContextHolder.setDataSource("master");
        List<String> dbNames = merchantMapper.findAllTenantDatabaseNames();

        // 2. 循环切换到每个租户库
        for (String dbName : dbNames) {
            // 如果租户数据源还没注册，则添加
            if (!dynamicDataSource.containsDataSource(dbName)) {
                dynamicDataSource.addDataSource(dbName,
                        TenantDataSourceProvider.createTenantDataSource(dbName));
            }
            DataSourceContextHolder.setDataSource(dbName);

            // 查询租户库中的所有用户
            List<UserInfo> users = userInfoMapper.findAllUsers();
            allUsers.addAll(users);
        }

        // 3. 切回 master
        DataSourceContextHolder.clear();
        return allUsers;
    }

    @Override
    public String getPasswordByUsername(String username) {
        AdminUser admin = adminUserMapper.findByUsername(username);
        return admin.getPassword();
    }

    @Override
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        AdminUser adminUser = adminUserMapper.findByUsername(username);
        if (adminUser == null ) {
            throw new RuntimeException("用户不存在");
        }
        if (!PasswordUtil.matches(oldPassword, adminUser.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        String encrypted = PasswordUtil.encrypt(newPassword);
        return adminUserMapper.updatePassword(username, encrypted) > 0;
    }

    @Override
    public boolean updateMerchantInfo(UpdateMerchantRegistryDTO merchantInfo) {
        return adminUserMapper.updateMerchantInfo(merchantInfo.getMerchant_id(), merchantInfo) > 0;
    }


    @Override
    public MerchantRegistry findByMerchantName(String merchantName) {
        return merchantMapper.selectByMerchantName(merchantName);
    }

    // 通过MerchantId删除用户
    @Override
    public void deleteUserByMerchantId(String merchantID) {


        // 删除merchantID对应的数据库
        String dbName = merchantMapper.getDatabaseNameByMerchantId(merchantID);
        System.out.println("dbName========================="+dbName);
        adminUserMapper.deleteDataBase(dbName);
        // 删除merchat_register表中数据
        adminUserMapper.deleteByMerchantId(merchantID);
    }


    @Override
    public void createTableAndInsert(String tableName, String merchantId, DeviceInfo deviceInfo) {
        String databaseName = merchantMapper.getDatabaseNameByMerchantId(merchantId);
        if (databaseName == null) {
            throw new RuntimeException("Invalid merchantId");
        }

        // 如果租户数据源还没注册，则创建并添加
        if (!dynamicDataSource.containsDataSource(databaseName)) {
            dynamicDataSource.addDataSource(databaseName, TenantDataSourceProvider.createTenantDataSource(databaseName));
        }
        // 切换数据源
        DataSourceContextHolder.setDataSource(databaseName);

        try {
            adminUserMapper.createTable(tableName);
            adminUserMapper.createTable_allDevice();
            if (deviceInfo != null) {
                deviceInfo.setUpdate_time(LocalDateTime.now());
                deviceInfo.setCreate_time(LocalDateTime.now());
                deviceInfo.setMerchant_id(merchantId);
                deviceInfo.setIs_disabled(false);
            }
            adminUserMapper.insertDevice(tableName, deviceInfo);
            adminUserMapper.insertAllDevice(tableName);
        } finally {
            DataSourceContextHolder.clear();
        }
    }

    @Override
    public void editDevice(String tableName, String merchantName, DeviceInfo deviceInfo) {
        String merchantId = merchantMapper.getMerchantIdByMerchantName(merchantName);
        String databaseName = merchantMapper.getDatabaseNameByMerchantId(merchantId);
        if (databaseName == null) {
            throw new RuntimeException("Invalid merchantId");
        }

        // 如果租户数据源还没注册，则创建并添加
        if (!dynamicDataSource.containsDataSource(databaseName)) {
            dynamicDataSource.addDataSource(databaseName, TenantDataSourceProvider.createTenantDataSource(databaseName));
        }
        // 切换数据源
        DataSourceContextHolder.setDataSource(databaseName);
        try {
            deviceInfo.setUpdate_time(LocalDateTime.now());
            deviceInfo.setMerchant_id(merchantId);
            adminUserMapper.updateDevice(merchantId,tableName, deviceInfo);
        } finally {
            DataSourceContextHolder.clear();
        }

    }

    @Override
    public DeviceInfo getDevices(String merchantId, String tableName) {
        String databaseName = merchantMapper.getDatabaseNameByMerchantId(merchantId);
        if (databaseName == null) {
            throw new RuntimeException("Invalid merchantId");
        }

        // 如果租户数据源还没注册，则创建并添加
        if (!dynamicDataSource.containsDataSource(databaseName)) {
            dynamicDataSource.addDataSource(databaseName, TenantDataSourceProvider.createTenantDataSource(databaseName));
        }
        // 切换数据源
        DataSourceContextHolder.setDataSource(databaseName);
        try {
            return adminUserMapper.getDevices(tableName);
        } finally {
            DataSourceContextHolder.clear();
        }
    }

    @Override
    public void deleteDevice(String merchantName, String tableName) {
        String merchantId = merchantMapper.getMerchantIdByMerchantName(merchantName);
        String databaseName = merchantMapper.getDatabaseNameByMerchantId(merchantId);
        if (databaseName == null) {
            throw new RuntimeException("Invalid merchantId");
        }

        // 如果租户数据源还没注册，则创建并添加
        if (!dynamicDataSource.containsDataSource(databaseName)) {
            dynamicDataSource.addDataSource(databaseName, TenantDataSourceProvider.createTenantDataSource(databaseName));
        }
        // 切换数据源
        DataSourceContextHolder.setDataSource(databaseName);
        try {
            adminUserMapper.deleteDevice(tableName);
            adminUserMapper.deleteAllDevice(tableName);
        } finally {
            DataSourceContextHolder.clear();
        }
    }

    @Override
    public void insetDeviceInfo(String tableName, String merchantId, DeviceInfo deviceInfo) {
        String databaseName = merchantMapper.getDatabaseNameByMerchantId(merchantId);
        if (databaseName == null) {
            throw new RuntimeException("Invalid merchantId");
        }

        // 如果租户数据源还没注册，则创建并添加
        if (!dynamicDataSource.containsDataSource(databaseName)) {
            dynamicDataSource.addDataSource(databaseName, TenantDataSourceProvider.createTenantDataSource(databaseName));
        }
        // 切换数据源
        DataSourceContextHolder.setDataSource(databaseName);
        try {
            deviceInfo.setUpdate_time(LocalDateTime.now());
            deviceInfo.setCreate_time(LocalDateTime.now());
            deviceInfo.setMerchant_id(merchantId);
            adminUserMapper.insertDevice(tableName, deviceInfo);
        } finally {
            DataSourceContextHolder.clear();
        }
    }

    @Override
    public List<AllMerchantDeviceInfoDTO> getAllDeviceInfo(String merchantNameLike) {
        List<String> merchantIds = merchantMapper.getMerchantIdsByMerchantNameLike(merchantNameLike);
        if (merchantIds == null || merchantIds.isEmpty()) {
            throw new RuntimeException("未找到匹配的商户");
        }

        List<AllMerchantDeviceInfoDTO> resultList = new ArrayList<>();

        for (String merchantId : merchantIds) {
            String databaseName = merchantMapper.getDatabaseNameByMerchantId(merchantId);
            if (databaseName == null) {
                continue;
            }

            // 数据源不存在则注册
            if (!dynamicDataSource.containsDataSource(databaseName)) {
                dynamicDataSource.addDataSource(databaseName, TenantDataSourceProvider.createTenantDataSource(databaseName));
            }
            //查找merchantNAme
            String merchantName = merchantMapper.selectByMerchantId(merchantId).getMerchant_name();
            DataSourceContextHolder.setDataSource(databaseName);
            try {
                // 检查 all_device 表是否存在
                if (!adminUserMapper.checkAllDeviceTableExists()) {
                    continue; // 跳过没有 all_device 表的库
                }

                // 获取设备信息
                List<String> deviceNames = adminUserMapper.getAllDeviceNames();
                List<AllDeviceDTO> deviceList = new ArrayList<>();

                for (String deviceName : deviceNames) {
                    try {
                        DeviceInfo data = adminUserMapper.getDevices(deviceName);
                        if (data != null) {
                            AllDeviceDTO dto = new AllDeviceDTO();
                            dto.setDeviceName(deviceName);
                            dto.setData(data);
                            deviceList.add(dto);
                        }
                    } catch (Exception ignored) {
                        // 单个设备表不存在或异常，直接跳过
                    }
                }
                // 组装商户设备信息
                AllMerchantDeviceInfoDTO merchantDeviceInfo = new AllMerchantDeviceInfoDTO();
                merchantDeviceInfo.setMerchantName(merchantName); // 这里用库名
                merchantDeviceInfo.setDevices(deviceList);

                resultList.add(merchantDeviceInfo);

            } finally {
                DataSourceContextHolder.clear();
            }
        }

        return resultList;
    }


    /**
     * 分页查询所有租户库设备信息
     * @param pageNum 页码，从1开始
     * @param pageSize 每页多少个租户库
     */
    @Override
    public Page<AllMerchantDeviceInfoDTO> getAllCompanyDevices(int pageNum, int pageSize) {
        // 1. 切主库
        DataSourceContextHolder.setDataSource("master");

        List<MerchantRegistry> merchants = merchantMapper.findAllMerchants();

        // 过滤出包含all_device表的商户
        List<MerchantRegistry> validMerchants = new ArrayList<>();
        for (MerchantRegistry merchant : merchants) {
            String dbName = merchant.getDatabase_name();
            // 注册数据源（如果没有）
            if (!dynamicDataSource.containsDataSource(dbName)) {
                dynamicDataSource.addDataSource(dbName, TenantDataSourceProvider.createTenantDataSource(dbName));
            }
            DataSourceContextHolder.setDataSource(dbName);

            try {
                // 检查all_device表是否存在
                if (adminUserMapper.checkTableExists("all_device")) {
                    validMerchants.add(merchant);
                }
            } catch (Exception e) {
                // 如果检查表存在性时出错，跳过该公司
                continue;
            } finally {
                DataSourceContextHolder.clear();
            }
        }

        // 2. 分页计算
        int total = validMerchants.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        if (fromIndex >= total) {
            return new Page<>(pageNum, pageSize, total, Collections.emptyList());
        }

        List<MerchantRegistry> pageMerchants = validMerchants.subList(fromIndex, toIndex);
        List<AllMerchantDeviceInfoDTO> results = new ArrayList<>();

        for (MerchantRegistry merchant : pageMerchants) {
            String dbName = merchant.getDatabase_name();
            String companyName = merchant.getMerchant_name();
            // 注册数据源（如果没有）
            if (!dynamicDataSource.containsDataSource(dbName)) {
                dynamicDataSource.addDataSource(dbName, TenantDataSourceProvider.createTenantDataSource(dbName));
            }
            DataSourceContextHolder.setDataSource(dbName);

            try {
                // 先查该库所有设备表名（在all_device表里）
                List<String> deviceTableNames = adminUserMapper.getAllDeviceNames();

                List<AllDeviceDTO> devices = new ArrayList<>();

                for (String tableName : deviceTableNames) {
                    try {
                        // 查该表最大id的设备记录
                        DeviceInfo deviceInfo = adminUserMapper.getDevices(tableName);
                        if (deviceInfo != null) {
                            AllDeviceDTO dto = new AllDeviceDTO();
                            dto.setDeviceName(tableName);
                            dto.setData(deviceInfo);
                            devices.add(dto);
                        }
                    } catch (Exception e) {
                        // 如果某个设备表不存在或查询出错，继续处理其他表
                        continue;
                    }
                }
                AllMerchantDeviceInfoDTO dto_ = new AllMerchantDeviceInfoDTO();
                dto_.setMerchantName(companyName);
                dto_.setDevices(devices);
                results.add(dto_);
            } catch (Exception e) {
                // 如果处理过程中出错，跳过该公司
                continue;
            } finally {
                DataSourceContextHolder.clear();
            }
        }

        return new Page<>(pageNum, pageSize, total, results);
    }

    /**
     * 禁用设备
     * @param disableDeviceDTO 禁用设备请求参数
     */
    @Override
    public void disableDevice(DisableDeviceDTO disableDeviceDTO) {
        String merchantName = disableDeviceDTO.getMerchantName();
        String merchantId = merchantMapper.getMerchantIdByMerchantName(merchantName);
        String databaseName = merchantMapper.getDatabaseNameByMerchantId(merchantId);
        String deviceName = disableDeviceDTO.getDeviceName();
        Boolean isDisabled = disableDeviceDTO.getIsDisabled();
        // 切换源
        if (databaseName == null) {
            throw new RuntimeException("Invalid merchantId");
        }
        // 如果租户数据源还没注册，则创建并添加
        if (!dynamicDataSource.containsDataSource(databaseName)) {
            dynamicDataSource.addDataSource(databaseName, TenantDataSourceProvider.createTenantDataSource(databaseName));
        }
        // 切换数据源
        DataSourceContextHolder.setDataSource(databaseName);

        try {
            adminUserMapper.disableDevice(deviceName,isDisabled);
        } finally {
            DataSourceContextHolder.clear();
        }
    }


}

