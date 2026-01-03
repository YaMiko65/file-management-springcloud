/**
 * 文件夹权限实体类
 * 用于表示用户对特定文件夹的访问权限
 */
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
    /**
     * 权限记录唯一标识ID
     */
    @TableId(type = IdType.AUTO)
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
     * 该字段不存入数据库，用于在查询时返回用户信息
     */
    @TableField(exist = false) // 表示该字段不对应数据库列
    private String username;
    /**
     * 用户真实姓名（非数据库字段）
     * 该字段不存入数据库，用于在查询时返回用户信息
     */
    @TableField(exist = false)
    private String userFullName; // 用户姓名
}