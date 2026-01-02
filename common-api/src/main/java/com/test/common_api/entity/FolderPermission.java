package com.test.common_api.entity;

import lombok.Data;

import java.util.Date;

@Data
public class FolderPermission {
    private Long id;
    private Long folderId; // 文件夹ID
    private Long userId; // 用户ID
    private Integer permission; // 1-只读，2-读写，3-管理
    private Date createTime;
    private Date updateTime;
    // 用于存储用户账号（非数据库字段）
    private String username;
    private String userFullName; // 用户姓名
}