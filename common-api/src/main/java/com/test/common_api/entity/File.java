package com.test.common_api.entity;

import lombok.Data;

import java.util.Date;

@Data
public class File {
    private Long id;
    private String fileName; // 文件名
    private String filePath; // 文件存储路径
    private Long fileSize; // 文件大小
    private String fileType; // 文件类型
    private Long userId; // 上传用户ID
    private String username; // 账号（登录名）
    private String userRealName;// 用户姓名
    private Long folderId; // 所属文件夹ID（关联文件夹）
    private Date createTime;
    private Date updateTime;

}