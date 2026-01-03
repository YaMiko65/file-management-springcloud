package com.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.test.entity.OperationLog;

import java.util.List;

/**
 * 操作日志服务接口
 * 定义操作日志的记录、查询和删除等核心功能
 */
public interface OperationLogService extends IService<OperationLog> {
    
    /**
     * 记录操作日志
     * 记录用户对文件的操作行为，包括操作类型、时间、IP地址等信息
     *
     * @param userId 执行操作的用户ID
     * @param fileId 被操作的文件ID
     * @param operationType 操作类型（upload-上传，download-下载，delete-删除）
     * @param ipAddress 客户端IP地址
     * @return 记录是否成功
     */
    boolean recordLog(Long userId, Long fileId, String operationType, String ipAddress);
    
    /**
     * 根据用户ID获取操作日志
     * 获取指定用户的所有操作日志记录
     *
     * @param userId 用户ID
     * @return 该用户的操作日志列表
     */
    List<OperationLog> getByUserId(Long userId);
    
    /**
     * 获取所有操作日志
     * 获取系统中的所有操作日志记录
     *
     * @return 所有操作日志列表
     */
    List<OperationLog> getAll();

    /**
     * 根据文件ID删除操作日志
     * 删除与指定文件相关的所有操作日志记录
     *
     * @param fileId 文件ID
     * @return 删除是否成功
     */
    boolean deleteByFileId(Long fileId);

    /**
     * 获取所有操作日志（分页）
     * 获取系统中的所有操作日志记录，并支持分页显示
     *
     * @param page 分页对象
     * @return 分页的操作日志列表
     */
    IPage<OperationLog> getAll(Page<OperationLog> page);

    /**
     * 根据用户ID删除所有操作日志
     * 删除指定用户的所有操作日志记录
     *
     * @param userId 用户ID
     * @return 删除是否成功
     */
    boolean deleteByUserId(Long userId);
}