package com.test.common_api.entity;

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
public class OperationLog {
    private Long id;
    private Long userId;
    private String username; // 用户名（账号）
    private String userRealName;
    private Long fileId;
    private String fileName;
    private String operationType; // upload-上传，download-下载，delete-删除
    private Date operationTime;
    private String ipAddress;

}