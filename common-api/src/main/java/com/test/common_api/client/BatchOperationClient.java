package com.test.common_api.client;

import com.test.common_api.entity.ExcelData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "batchService")
public interface BatchOperationClient {
    @PostMapping("/processExcelData")
    public Map<String, Integer> processExcelData(@RequestBody List<ExcelData> dataList,@RequestParam("adminId") Long adminId);



}
