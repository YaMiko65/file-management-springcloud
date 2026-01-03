package com.test.common_api.client;

import com.test.common_api.entity.ExcelData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 批量操作服务客户端接口
 * 提供与其他服务通信的批量操作相关API接口
 */
@FeignClient(name = "batchService")
public interface BatchOperationClient {
    /**
     * 处理Excel数据
     * 批量处理从Excel文件中解析出的数据
     *
     * @param dataList Excel数据列表
     * @param adminId 操作的管理员ID
     * @return 处理结果统计，包含成功和失败的数量
     */
    @PostMapping("/processExcelData")
    public Map<String, Integer> processExcelData(@RequestBody List<ExcelData> dataList,@RequestParam("adminId") Long adminId);



}