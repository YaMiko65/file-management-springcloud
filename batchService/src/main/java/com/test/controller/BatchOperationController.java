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

@RestController
@Slf4j
public class BatchOperationController {
    @Autowired
    private BatchOperationService batchOperationService;

    @PostMapping("/processExcelData")
    public Map<String, Integer> processExcelData(@RequestBody List<ExcelData> dataList, @RequestParam("adminId") Long adminId){
        log.debug("processExcelData:接收到的参数：dataList:{},adminId:{}",dataList,adminId);
        Map<String, Integer> result = batchOperationService.processExcelData(dataList, adminId);
        log.debug("batchOperationService.processExcelData:{}",result);
        return result;
    }
}
