package com.example.work.Service;

import com.example.work.dto.UUIDLatest;

public interface UploadService {
    UUIDLatest getLatestUUID(String table,String merchantId);
}
