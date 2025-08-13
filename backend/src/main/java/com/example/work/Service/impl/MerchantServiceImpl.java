package com.example.work.Service.impl;

import com.example.work.config.DynamicDataSource;
import com.example.work.dto.MerchantRegisterDTO;
import com.example.work.entity.master.MerchantRegistry;
import com.example.work.entity.tenant.UserInfo;
import com.example.work.mapper.master.MerchantMapper;
import com.example.work.Service.MerchantService;
import com.example.work.mapper.tenant.UserInfoMapper;
import com.example.work.util.DBNameUtil;
import com.example.work.util.DataSourceContextHolder;
import com.example.work.util.PasswordUtil;
import com.example.work.util.TenantDatabaseUtil;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantMapper merchantMapper;
    private final TenantDatabaseUtil tenantDatabaseUtil;
    private final UserInfoMapper userInfoMapper;

    private final DynamicDataSource dynamicDataSource;

    private DataSource createTenantDataSource(String dbName) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://localhost:3308/" + dbName + "?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai");
        ds.setUsername("root");
        ds.setPassword("123456");
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return ds;
    }
    /**
     * 注册商户：创建租户库、初始化表结构、写入主库信息
     */
    @Override
    public void registerMerchant(MerchantRegisterDTO dto) {
        // 准备商户信息
        String merchantId = UUID.randomUUID().toString();
        String dbName = DBNameUtil.generateDatabaseName(dto.getMerchant_name());

        // 创建数据库 + 表结构
        tenantDatabaseUtil.createDatabase(dbName);
        tenantDatabaseUtil.createTablesInTenantDb(dbName);

        // 写入 master_db 的 merchant_registry 表
        MerchantRegistry merchant = new MerchantRegistry();
        merchant.setMerchant_id(merchantId);
        merchant.setMerchant_name(dto.getMerchant_name());
        merchant.setContact_name(dto.getContact_name());
        merchant.setContact_phone(dto.getContact_phone());
        merchant.setEmail(dto.getEmail());
        merchant.setUsername(dto.getUsername());
        merchant.setPassword(PasswordUtil.encrypt(dto.getPassword()));  // 加密存储
        merchant.setDatabase_name(dbName);
        merchant.setRegister_time(LocalDateTime.now());
        merchant.setStatus(1); // 默认启用

        merchantMapper.insertMerchant(merchant);

        // 创建 DataSource 并注册到 DynamicDataSource
        DataSource tenantDs = createTenantDataSource(dbName);
        dynamicDataSource.addDataSource(dbName, tenantDs);

        try {
            // 切库到新租户库
            DataSourceContextHolder.setDataSource(dbName);
            System.out.println("==================切换到租户数据库：" + dbName);
            System.out.println("==================当前数据源：" + DataSourceContextHolder.getDataSource());
            // 强制检查当前线程的数据源
            String currentDataSource = DataSourceContextHolder.getDataSource();
            if (currentDataSource == null || !currentDataSource.equals(dbName)) {
                throw new RuntimeException("数据源切换失败，期望: " + dbName + ", 实际: " + currentDataSource);
            }
            // 往 user_info 插入初始账号信息
            UserInfo initUser = new UserInfo();
            initUser.setMerchant_id(merchantId);
            initUser.setUsername(dto.getUsername());
            initUser.setPassword(PasswordUtil.encrypt(dto.getPassword()));
            initUser.setReal_name(dto.getContact_name());
            initUser.setPhone(dto.getContact_phone());
            initUser.setEmail(dto.getEmail());
            initUser.setRole("一级用户");
            initUser.setCreate_time(LocalDateTime.now());
            initUser.setPayStatus(true);

            userInfoMapper.insert(initUser);
        } finally {
            // 切回主库master_db
            DataSourceContextHolder.clear();
        }

        log.info("✅ 商户注册完成：商户ID={}, 库名={}", merchantId, dbName);
    }

    @Override
    public List<MerchantRegistry> getAllMerchants() {
        return merchantMapper.findAllMerchants();
    }

    @Override
    public String getDatabaseNameByMerchantId(String merchantId) {
        return merchantMapper.getDatabaseNameByMerchantId(merchantId);
    }

    @Override
    public MerchantRegistry getMerchantsByMerchantId(String merchantId) {
        return merchantMapper.selectByMerchantId(merchantId);
    }


}
