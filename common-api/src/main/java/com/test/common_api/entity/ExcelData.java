package com.test.common_api.entity;

import lombok.Data;

@Data
public class ExcelData {
    private String folderName;    // 文件夹名称
    private String username;      //账号
    private String name;          //姓名
    private Integer permission;   // 权限类型(1-只读，2-读写，3-管理)

}