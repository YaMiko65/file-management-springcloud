package com.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 操作日志实体类
 */
@Data
@TableName("operation_log")
public class OperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    @TableField(exist = false) // 明确告诉 MyBatis-Plus 该字段不是数据库表中的列
    private String username; // 用户名（账号）
    @TableField(exist = false) // 用户姓名
    private String userRealName;
    private Long fileId;
    @TableField(exist = false) // 文件名（非数据库字段）
    private String fileName;
    private String operationType; // upload-上传，download-下载，delete-删除
    private Date operationTime;
    private String ipAddress;

}