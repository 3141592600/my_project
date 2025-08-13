package com.example.work.Service;

import com.example.work.dto.MerchantRegisterDTO;
import com.example.work.entity.master.MerchantRegistry;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MerchantService {

    void registerMerchant(MerchantRegisterDTO dto);

    List<MerchantRegistry> getAllMerchants();

    String getDatabaseNameByMerchantId(String merchantId);

    MerchantRegistry getMerchantsByMerchantId(String merchantId);
}