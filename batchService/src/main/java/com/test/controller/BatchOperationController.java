package com.test.controller;

import com.test.common_api.entity.ExcelData;
import com.test.service.BatchOperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 批量操作控制器
 * 提供批量处理Excel数据的REST API接口
 */
@RestController
@Slf4j
public class BatchOperationController {
    @Autowired
    private BatchOperationService batchOperationService;

    /**
     * 处理Excel数据
     * 批量处理从Excel文件中解析出的数据，如批量分配文件夹权限
     *
     * @param dataList Excel数据列表
     * @param adminId 操作的管理员ID
     * @return 处理结果统计，包含成功和失败的数量
     */
    @PostMapping("/processExcelData")
    public Map<String, Integer> processExcelData(@RequestBody List<ExcelData> dataList, @RequestParam("adminId") Long adminId){
        log.debug("processExcelData:接收到的参数：dataList:{},adminId:{}",dataList,adminId);
        Map<String, Integer> result = batchOperationService.processExcelData(dataList, adminId);
        log.debug("batchOperationService.processExcelData:{}",result);
        return result;
    }
}