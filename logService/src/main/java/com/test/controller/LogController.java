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
 * 提供操作日志记录和管理的REST API接口
 */
@RestController
@Slf4j
public class LogController {
    
    @Autowired
    private OperationLogService operationLogService;

    /**
     * 记录操作日志
     * 记录用户对文件的操作行为
     *
     * @param userId 执行操作的用户ID
     * @param fileId 被操作的文件ID
     * @param operationType 操作类型（upload-上传，download-下载，delete-删除）
     * @param ipAddress 客户端IP地址
     * @return 记录是否成功
     */
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

    /**
     * 根据用户ID删除操作日志
     *
     * @param userId 用户ID
     * @return 删除是否成功
     */
    @GetMapping("/deleteByUserId/{userId}")
    public boolean deleteByUserId(@PathVariable("userId") Long userId){
        log.debug("deleteByUserId:接收到的参数：userId:{}",userId);
        boolean b = operationLogService.deleteByUserId(userId);
        log.debug("operationLogService.operationLogService:{}",b);
        return b;
    }

    /**
     * 获取所有操作日志（分页）
     *
     * @param page 分页对象
     * @return 分页的操作日志列表
     */
    @PostMapping("/getAllByPage")
    public Page<OperationLog> getAll(@RequestBody Page<OperationLog> page){
        log.debug("getAll:接收到的参数：page:{}",page);
        IPage<OperationLog> logPage = operationLogService.getAll(page);
        log.debug("operationLogService.getAll:{}",logPage);
        return (Page<OperationLog>) logPage;
    }

    /**
     * 根据查询条件删除操作日志
     *
     * @param qw 查询条件
     * @return 删除是否成功
     */
    @PostMapping("/remove")
    public boolean remove(@RequestBody QueryWrapper<OperationLog> qw){
        log.debug("remove:接收到的参数：qw:{}",qw);
        boolean remove = operationLogService.remove(qw);
        log.debug("operationLogService.remove:{}",remove);
        return remove;
    }

    /**
     * 根据文件ID删除操作日志
     *
     * @param fileId 文件ID
     * @return 删除是否成功
     */
    @GetMapping("/deleteByFileId/{fileId}")
    public boolean deleteByFileId(@PathVariable("fileId") Long fileId){
        log.debug("deleteByFileId:接收到的参数：fileId:{}",fileId);
        boolean b = operationLogService.deleteByFileId(fileId);
        log.debug("operationLogService.deleteByFileId:{}",b);
        return b;
    }


}