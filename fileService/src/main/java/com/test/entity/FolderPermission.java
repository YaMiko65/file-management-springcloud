package com.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("folder_permission")
public class FolderPermission {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long folderId; // 文件夹ID
    private Long userId; // 用户ID
    private Integer permission; // 1-只读，2-读写，3-管理
    private Date createTime;
    private Date updateTime;
    // 用于存储用户账号（非数据库字段）
    @TableField(exist = false) // 表示该字段不对应数据库列
    private String username;
    @TableField(exist = false)
    private String userFullName; // 用户姓名
}