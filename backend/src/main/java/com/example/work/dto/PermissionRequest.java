package com.example.work.dto;

import lombok.Data;

import java.util.Map;

@Data
public class PermissionRequest {
    private String table_name;
    private String username;
    private Map<String, Boolean> fieldPermissions;
}
