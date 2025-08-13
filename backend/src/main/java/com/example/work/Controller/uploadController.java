package com.example.work.Controller;

import com.example.work.Service.UploadService;
import com.example.work.dto.UUIDLatest;
import com.example.work.dto.UploadDataDTO;
import com.example.work.entity.master.*;
import com.example.work.util.JwtUtil;
import com.example.work.util.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class uploadController {

    private final UploadService uploadService;
    private final JwtUtil jwtUtil;

    @GetMapping("/test")
    public String test() {
        return "SUCCESS";
    }

    /**
     * 获取最新 UUID
     */
    @GetMapping("/latestUUID")
    public UUIDLatest getLatestUUID(@RequestParam String table, HttpServletRequest  request) {
        String token = getToken(request);
        String merchantId = jwtUtil.getMerchantIdFromToken(token);
        return uploadService.getLatestUUID(table,merchantId);
    }

    /**
     * 上传数据
     */
    @PostMapping("/upload")
    public Result<?> upload(@RequestParam String table,
                            HttpServletRequest  request,
                            @RequestBody UploadDataDTO<?> uploadData){
        String token = getToken(request);
        log.info("收到上传请求，目标表：{}，主对象类型：{}，明细数量：{}",
                table,
                uploadData.getObj() == null ? "null" : uploadData.getObj().getClass().getSimpleName(),
                uploadData.getList() == null ? 0 : uploadData.getList().size());
        System.out.println("token================"+ token);
        try {
            // 根据表名进行类型转换（核心：将LinkedHashMap转为具体PO类）
            UploadDataDTO<?> convertedData = convertUploadData(table, uploadData);
            if (convertedData == null) {
                return Result.error("不支持的表名：" + table);
            }

            // 调用服务层处理上传逻辑
            String result = "uploadService.saveUploadData(convertedData, table)";
            return Result.success(result);
        } catch (Exception e) {
            log.error("上传数据转换失败", e);
            return Result.error("数据格式错误：" + e.getMessage());
        }
    }

    /**
     * 根据表名转换UploadData中的obj和list为具体类型
     * @param tableName 表名
     * @param uploadData 原始上传数据（含LinkedHashMap类型的obj和list）
     * @return 转换后的UploadData（含具体PO类型的obj和list）
     */
    private UploadDataDTO<?> convertUploadData(String tableName, UploadDataDTO<?> uploadData) {
        ObjectMapper objectMapper = new ObjectMapper();
        switch (tableName) {
            case "formula":
                // 转换配方表数据：obj->GzFormula，list->GzFormulaRecord
                GzFormula formula = objectMapper.convertValue(uploadData.getObj(), GzFormula.class);
                List<GzFormulaRecord> formulaRecords = convertList(uploadData.getList(), GzFormulaRecord.class, objectMapper);

                UploadDataDTO<GzFormula> formulaData = new UploadDataDTO<>();
                formulaData.setObj(formula);
                formulaData.setList(formulaRecords);
                return formulaData;

            case "supplier":
                // 转换供应商表数据：obj->GzSupplier
                GzSupplier supplier = objectMapper.convertValue(uploadData.getObj(), GzSupplier.class);

                UploadDataDTO<GzSupplier> supplierData = new UploadDataDTO<>();
                supplierData.setObj(supplier);
                supplierData.setList(uploadData.getList()); // 供应商表无明细，直接复用
                return supplierData;

            case "production_records":
                // 转换生产记录表数据：obj->GzProductionRecords，list->GzProductionRecordsDetail
                GzProductionRecords production = objectMapper.convertValue(uploadData.getObj(), GzProductionRecords.class);
                List<GzProductionRecordsDetail> productionDetails = convertList(uploadData.getList(), GzProductionRecordsDetail.class, objectMapper);

                UploadDataDTO<GzProductionRecords> productionData = new UploadDataDTO<>();
                productionData.setObj(production);
                productionData.setList(productionDetails);
                return productionData;

            case "raw_material_storage_record":
                // 转换原料入库表数据：obj->GzRawMaterialStorageRecord
                GzRawMaterialStorageRecord storage = objectMapper.convertValue(uploadData.getObj(), GzRawMaterialStorageRecord.class);

                UploadDataDTO<GzRawMaterialStorageRecord> storageData = new UploadDataDTO<>();
                storageData.setObj(storage);
                storageData.setList(uploadData.getList()); // 原料入库表无明细，直接复用
                return storageData;

            default:
                log.warn("不支持的表名转换：{}", tableName);
                return null;
        }
    }

    /**
     * 通用方法：将List<LinkedHashMap>转换为List<具体PO类>
     * @param sourceList 原始列表（含LinkedHashMap）
     * @param targetClass 目标PO类的Class对象
     * @param objectMapper Jackson转换工具
     * @return 转换后的PO列表
     */
    private <T> List<T> convertList(List<?> sourceList, Class<T> targetClass, ObjectMapper objectMapper) {
        if (sourceList == null || sourceList.isEmpty()) {
            return null;
        }
        return sourceList.stream()
                .map(item -> objectMapper.convertValue(item, targetClass))
                .collect(Collectors.toList());
    }

    /**
     * 从请求头中提取 token
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        throw new RuntimeException("Token 不存在或格式错误");
    }
}
