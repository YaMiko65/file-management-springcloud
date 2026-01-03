package com.test.common_api.entity;

import lombok.Data;

/**
 * Excel数据实体类
 * 用于批量导入/导出文件夹权限信息
 */
@Data
public class ExcelData {
    /**
     * 文件夹名称
     */
    private String folderName;    // 文件夹名称
    /**
     * 用户账号
     */
    private String username;      //账号
    /**
     * 用户真实姓名
     */
    private String name;          //姓名
    /**
     * 权限类型：1-只读，2-读写，3-管理
     */
    private Integer permission;   // 权限类型(1-只读，2-读写，3-管理)

}