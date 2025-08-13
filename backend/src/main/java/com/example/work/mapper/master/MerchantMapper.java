package com.example.work.mapper.master;

import com.example.work.entity.master.MerchantRegistry;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MerchantMapper {

    void insertMerchant(MerchantRegistry merchantRegistry);

    boolean existsByUsername(String username);

    @Select("SELECT * FROM merchant_registry WHERE username = #{username}")
    MerchantRegistry findByUsername(String username);

    MerchantRegistry selectByMerchantId(String merchantId);

    @Select("SELECT * FROM merchant_registry")
    List<MerchantRegistry> findAllMerchants();

    @Select("SELECT database_name FROM merchant_registry WHERE merchant_id = #{merchantId}")
    String getDatabaseNameByMerchantId(@Param("merchantId") String merchantId);

    @Select("SELECT database_name FROM merchant_registry")
    List<String> findAllTenantDatabaseNames();

    @Select("SELECT * FROM merchant_registry WHERE merchant_name = #{merchantName}")
    MerchantRegistry selectByMerchantName(String merchantName);

    @Select("SELECT merchant_id FROM merchant_registry WHERE merchant_name = #{merchantName}")
    String getMerchantIdByMerchantName(String merchantName);

    @Select("SELECT merchant_id FROM merchant_registry WHERE merchant_name LIKE CONCAT('%', #{merchantName}, '%')")
    List<String> getMerchantIdsByMerchantNameLike(@Param("merchantName") String merchantName);
}
