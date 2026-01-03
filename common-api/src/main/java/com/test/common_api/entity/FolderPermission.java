package com.test.common_api.entity;

import lombok.Data;

import java.util.Date;

/**
 * 文件夹权限实体类
 * 用于表示用户对特定文件夹的访问权限
 */
@Data
public class FolderPermission {
    /**
     * 权限记录唯一标识ID
     */
    private Long id;
    /**
     * 文件夹ID
     */
    private Long folderId; // 文件夹ID
    /**
     * 用户ID
     */
    private Long userId; // 用户ID
    /**
     * 权限类型：1-只读，2-读写，3-管理
     */
    private Integer permission; // 1-只读，2-读写，3-管理
    /**
     * 权限创建时间
     */
    private Date createTime;
    /**
     * 权限信息更新时间
     */
    private Date updateTime;
    /**
     * 用于存储用户账号（非数据库字段）
     */
    private String username;
    /**
     * 用户真实姓名（非数据库字段）
     */
    private String userFullName; // 用户姓名
}