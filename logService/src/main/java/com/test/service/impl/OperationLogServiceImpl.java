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
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {
    
    @Autowired
    private OperationLogMapper operationLogMapper;
    
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
    
    @Override
    @Transactional
    public List<OperationLog> getByUserId(Long userId) {
        return operationLogMapper.selectByUserId(userId);
    }
    
    @Override
    @Transactional
    public List<OperationLog> getAll() {
        return operationLogMapper.selectAll();
    }

    @Override
    @Transactional
    public boolean deleteByFileId(Long fileId) {
        QueryWrapper<OperationLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("file_id", fileId);
        baseMapper.delete(queryWrapper);
        return true;
    }

    @Override
    @Transactional
    public IPage<OperationLog> getAll(Page<OperationLog> page) {
        return operationLogMapper.selectOperationLogPage(page);
    }


    @Override
    @Transactional
    public boolean deleteByUserId(Long userId) {
        QueryWrapper<OperationLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        baseMapper.delete(queryWrapper);
        return true;
    }
}