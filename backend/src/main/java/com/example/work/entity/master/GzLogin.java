package com.example.work.entity.master;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("gz_login")
public class GzLogin {
    // 主键ID
    private Integer id;
    // 用户名
    private String user;
    // 密码
    private String pass;
}
