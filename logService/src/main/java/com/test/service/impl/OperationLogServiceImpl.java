package com.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.entity.OperationLog;
import com.test.mapper.OperationLogMapper;
import com.test.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 操作日志服务实现类
 * 实现操作日志的记录、查询和删除功能
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {
    
    @Autowired
    private OperationLogMapper operationLogMapper;
    
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
    @Override
    @Transactional
    public boolean recordLog(Long userId, Long fileId, String operationType, String ipAddress) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setFileId(fileId);
        log.setOperationType(operationType);
        log.setOperationTime(new Date());
        log.setIpAddress(ipAddress);

        return save(log);

    }
    
    /**
     * 根据用户ID获取操作日志列表
     *
     * @param userId 用户ID
     * @return 该用户的操作日志列表
     */
    @Override
    @Transactional
    public List<OperationLog> getByUserId(Long userId) {
        return operationLogMapper.selectByUserId(userId);
    }
    
    /**
     * 获取所有操作日志列表
     *
     * @return 所有操作日志列表
     */
    @Override
    @Transactional
    public List<OperationLog> getAll() {
        return operationLogMapper.selectAll();
    }

    /**
     * 根据文件ID删除操作日志
     * 删除与指定文件相关的所有操作日志记录
     *
     * @param fileId 文件ID
     * @return 删除是否成功
     */
    @Override
    @Transactional
    public boolean deleteByFileId(Long fileId) {
        QueryWrapper<OperationLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("file_id", fileId);
        baseMapper.delete(queryWrapper);
        return true;
    }

    /**
     * 获取所有操作日志（分页）
     *
     * @param page 分页对象
     * @return 分页的操作日志列表
     */
    @Override
    @Transactional
    public IPage<OperationLog> getAll(Page<OperationLog> page) {
        return operationLogMapper.selectOperationLogPage(page);
    }


    /**
     * 根据用户ID删除操作日志
     * 删除指定用户的所有操作日志记录
     *
     * @param userId 用户ID
     * @return 删除是否成功
     */
    @Override
    @Transactional
    public boolean deleteByUserId(Long userId) {
        QueryWrapper<OperationLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        baseMapper.delete(queryWrapper);
        return true;
    }
}