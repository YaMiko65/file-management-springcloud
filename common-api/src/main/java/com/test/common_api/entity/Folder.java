package com.test.common_api.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Folder {
    private Long id;
    private String name; // 文件夹名称
    private Long creatorId; // 创建者ID（管理员）
    private Date createTime;
    private Date updateTime;
}