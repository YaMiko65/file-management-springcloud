package com.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("file")
public class File {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String fileName; // 文件名
    private String filePath; // 文件存储路径
    private Long fileSize; // 文件大小
    private String fileType; // 文件类型
    private Long userId; // 上传用户ID
    @TableField(exist = false) // 明确告诉 MyBatis-Plus 该字段不是数据库表中的列
    private String username; // 账号（登录名）
    @TableField(exist = false)
    private String userRealName;// 用户姓名
    private Long folderId; // 所属文件夹ID（关联文件夹）
    private Date createTime;
    private Date updateTime;

    // 新增：用于存储查询时联表获取的权限值
    @TableField(exist = false)
    private Integer currentUserPermission;
}