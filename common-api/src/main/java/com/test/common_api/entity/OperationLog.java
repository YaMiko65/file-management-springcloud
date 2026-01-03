package com.test.common_api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 操作日志实体类
 * 用于记录系统中用户对文件的各种操作
 */
@Data
public class OperationLog {
    /**
     * 日志记录唯一标识ID
     */
    private Long id;
    /**
     * 执行操作的用户ID
     */
    private Long userId;
    /**
     * 执行操作的用户名（账号）
     */
    private String username; // 用户名（账号）
    /**
     * 执行操作的用户真实姓名
     */
    private String userRealName;
    /**
     * 被操作的文件ID
     */
    private Long fileId;
    /**
     * 被操作的文件名称
     */
    private String fileName;
    /**
     * 操作类型：upload-上传，download-下载，delete-删除
     */
    private String operationType; // upload-上传，download-下载，delete-删除
    /**
     * 操作执行的时间
     */
    private Date operationTime;
    /**
     * 执行操作的客户端IP地址
     */
    private String ipAddress;

}