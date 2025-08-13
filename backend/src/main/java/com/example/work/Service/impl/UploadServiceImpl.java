package com.example.work.Service.impl;

import com.example.work.Service.UploadService;
import com.example.work.config.DynamicDataSource;
import com.example.work.dto.UUIDLatest;
import com.example.work.mapper.master.MerchantMapper;
import com.example.work.mapper.master.UploadServiceMapper;
import com.example.work.util.DataSourceContextHolder;
import com.example.work.util.TenantDataSourceProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;

@Service
@AllArgsConstructor
@Slf4j
public class UploadServiceImpl implements UploadService {

    private final UploadServiceMapper uploadServiceMapper;
    private final MerchantMapper merchantMapper;
    private final DynamicDataSource dynamicDataSource;
    @Override
    public UUIDLatest getLatestUUID(String table,String merchantId) {
        // 初始化响应对象
        UUIDLatest result = new UUIDLatest();
        result.setCode(200); // 默认成功状态码
//        // 获取数据库名称
//        String databaseName = merchantMapper.getDatabaseNameByMerchantId(merchantId);
//        if (databaseName == null) {
//            throw new RuntimeException("Invalid merchantId");
//        }
//        // 如果租户数据源还没注册，则创建并添加
//        if (!dynamicDataSource.containsDataSource(databaseName)) {
//            dynamicDataSource.addDataSource(databaseName, TenantDataSourceProvider.createTenantDataSource(databaseName));
//        }
//        // 切换数据源
//        DataSourceContextHolder.setDataSource(databaseName);
        try {
            // 根据表名查询最新UUID（按create_time倒序取第一条）
            String uuid = null;
            switch (table) {
                case "supplier":
                    uuid = uploadServiceMapper.getLatestUUIDFromTable("gz_supplier");
                    break;
                case "formula":
                    uuid = uploadServiceMapper.getLatestUUIDFromTable("gz_formula");
                    break;
                case "production_records":
                    uuid = uploadServiceMapper.getLatestUUIDFromTable("gz_production_records");
                    break;
                case "raw_material_storage_record":
                    uuid = uploadServiceMapper.getLatestUUIDFromTable("gz_raw_material_storage_record");
                    break;
                default:
                    result.setCode(400); // 自定义错误码：不支持的表名
                    result.setExistUUID(false);
                    return result;
            }

            // 3. 封装返回结果
            result.setExistUUID(uuid != null);
            result.setUUID(uuid);

        } catch (Exception e) {
            log.error("获取最新UUID失败", e);
            result.setCode(500); // 自定义错误码：数据库异常
            result.setExistUUID(false);
        }
        return result;
    }
}
