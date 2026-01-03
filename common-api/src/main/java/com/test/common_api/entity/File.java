package com.test.common_api.entity;

import lombok.Data;

import java.util.Date;

/**
 * 文件实体类
 * 用于表示系统中存储的文件信息
 */
@Data
public class File {
    /**
     * 文件唯一标识ID
     */
    private Long id;
    /**
     * 文件原始名称
     */
    private String fileName; // 文件名
    /**
     * 文件在服务器上的存储路径
     */
    private String filePath; // 文件存储路径
    /**
     * 文件大小（字节）
     */
    private Long fileSize; // 文件大小
    /**
     * 文件类型（扩展名）
     */
    private String fileType; // 文件类型
    /**
     * 上传该文件的用户ID
     */
    private Long userId; // 上传用户ID
    /**
     * 上传该文件的用户账号（登录名）
     */
    private String username; // 账号（登录名）
    /**
     * 上传该文件的用户真实姓名
     */
    private String userRealName;// 用户姓名
    /**
     * 文件所属的文件夹ID
     */
    private Long folderId; // 所属文件夹ID（关联文件夹）
    /**
     * 文件创建时间
     */
    private Date createTime;
    /**
     * 文件信息更新时间
     */
    private Date updateTime;

    /**
     * 当前用户对该文件的权限（1-只读，2-读写，3-管理）
     * 该字段不存入文件表，而是通过关联查询得出
     */
    private Integer currentUserPermission;
}