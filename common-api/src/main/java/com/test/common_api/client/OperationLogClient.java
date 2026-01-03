package com.test.common_api.client;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.test.common_api.entity.OperationLog;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 操作日志服务客户端接口
 * 提供与其他服务通信的操作日志相关API接口
 */
@FeignClient(name = "logService")
public interface OperationLogClient {
    /**
     * 记录操作日志
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
    );
    
    /**
     * 根据用户ID删除操作日志
     *
     * @param userId 用户ID
     * @return 删除是否成功
     */
    @GetMapping("/deleteByUserId/{userId}")
    public boolean deleteByUserId(@PathVariable("userId") Long userId);
    
    /**
     * 获取所有操作日志（分页）
     *
     * @param page 分页对象
     * @return 分页的操作日志列表
     */
    @PostMapping("/getAllByPage")
    public Page<OperationLog> getAll(@RequestBody Page<OperationLog> page);
    
    /**
     * 根据查询条件删除操作日志
     *
     * @param qw 查询条件
     * @return 删除是否成功
     */
    @PostMapping("/remove")
    public boolean remove(@RequestBody QueryWrapper<OperationLog> qw);
    
    /**
     * 根据文件ID删除操作日志
     *
     * @param fileId 文件ID
     * @return 删除是否成功
     */
    @GetMapping("/deleteByFileId/{fileId}")
    public boolean deleteByFileId(@PathVariable("fileId") Long fileId);
}
