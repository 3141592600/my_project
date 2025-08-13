package com.example.work.util;
//数据库名称生成工具类

import java.text.SimpleDateFormat;
import java.util.Date;

public class DBNameUtil {
    public static String generateDatabaseName(String merchantName) {
        return "tenant_" + merchantName.toLowerCase().replaceAll("\\s+", "_")
                + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }
}
