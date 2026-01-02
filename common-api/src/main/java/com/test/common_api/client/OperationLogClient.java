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

@FeignClient(name = "logService")
public interface OperationLogClient {
    @GetMapping("/recordLog/{userId}/{fileId}/{operationType}/{ipAddress}")
    public boolean recordLog(
            @PathVariable("userId") Long userId,
            @PathVariable("fileId") Long fileId,
            @PathVariable("operationType") String operationType,
            @PathVariable("ipAddress") String ipAddress
    );
    @GetMapping("/deleteByUserId/{userId}")
    public boolean deleteByUserId(@PathVariable("userId") Long userId);
    @PostMapping("/getAllByPage")
    public Page<OperationLog> getAll(@RequestBody Page<OperationLog> page);
    @PostMapping("/remove")
    public boolean remove(@RequestBody QueryWrapper<OperationLog> qw);
    @GetMapping("/deleteByFileId/{fileId}")
    public boolean deleteByFileId(@PathVariable("fileId") Long fileId);
}
