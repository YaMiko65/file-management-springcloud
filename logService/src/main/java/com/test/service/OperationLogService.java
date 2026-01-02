package com.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.test.entity.OperationLog;

import java.util.List;

/**
 * 操作日志服务接口
 */
public interface OperationLogService extends IService<OperationLog> {
    
    /**
     * 记录操作日志
     * @param userId 用户ID
     * @param fileId 文件ID
     * @param operationType 操作类型
     * @param ipAddress IP地址
     */
    boolean recordLog(Long userId, Long fileId, String operationType, String ipAddress);
    
    /**
     * 根据用户ID获取操作日志
     * @param userId 用户ID
     * @return 操作日志列表
     */
    List<OperationLog> getByUserId(Long userId);
    
    /**
     * 获取所有操作日志
     * @return 操作日志列表
     */
    List<OperationLog> getAll();

    /**
     * 根据文件ID删除操作日志
     * @param fileId 文件ID
     */
    boolean deleteByFileId(Long fileId);

    // 添加分页查询方法
    IPage<OperationLog> getAll(Page<OperationLog> page);

    // 新增方法：根据用户ID删除所有操作日志
    boolean deleteByUserId(Long userId);
}