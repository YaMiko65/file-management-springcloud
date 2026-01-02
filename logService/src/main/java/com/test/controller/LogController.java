package com.test.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.test.entity.OperationLog;
import com.test.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志控制器
 */
@RestController
@Slf4j
public class LogController {
    
    @Autowired
    private OperationLogService operationLogService;

    @GetMapping("/recordLog/{userId}/{fileId}/{operationType}/{ipAddress}")
    public boolean recordLog(
            @PathVariable("userId") Long userId,
            @PathVariable("fileId") Long fileId,
            @PathVariable("operationType") String operationType,
            @PathVariable("ipAddress") String ipAddress
    ){
        log.debug("recordLog:接收到的参数：userId:{}\nfileId:{}\noperationType:{}\nipAddress:{}",userId,fileId,operationType,ipAddress);
        boolean b = operationLogService.recordLog(userId, fileId, operationType, ipAddress);
        log.debug("operationLogService.recordLog:{}",b);
        return b;
    }
    @GetMapping("/deleteByUserId/{userId}")
    public boolean deleteByUserId(@PathVariable("userId") Long userId){
        log.debug("deleteByUserId:接收到的参数：userId:{}",userId);
        boolean b = operationLogService.deleteByUserId(userId);
        log.debug("operationLogService.operationLogService:{}",b);
        return b;
    }
    @PostMapping("/getAllByPage")
    public Page<OperationLog> getAll(@RequestBody Page<OperationLog> page){
        log.debug("getAll:接收到的参数：page:{}",page);
        IPage<OperationLog> logPage = operationLogService.getAll(page);
        log.debug("operationLogService.getAll:{}",logPage);
        return (Page<OperationLog>) logPage;
    }
    @PostMapping("/remove")
    public boolean remove(@RequestBody QueryWrapper<OperationLog> qw){
        log.debug("remove:接收到的参数：qw:{}",qw);
        boolean remove = operationLogService.remove(qw);
        log.debug("operationLogService.remove:{}",remove);
        return remove;
    }
    @GetMapping("/deleteByFileId/{fileId}")
    public boolean deleteByFileId(@PathVariable("fileId") Long fileId){
        log.debug("deleteByFileId:接收到的参数：fileId:{}",fileId);
        boolean b = operationLogService.deleteByFileId(fileId);
        log.debug("operationLogService.deleteByFileId:{}",b);
        return b;
    }


}