/**
 * 文件实体类
 * 用于表示系统中存储的文件信息
 */
package com.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("file")
public class File {
    /**
     * 文件唯一标识ID
     */
    @TableId(type = IdType.AUTO)
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
     * 该字段不存入数据库，用于在查询时返回用户信息
     */
    @TableField(exist = false) // 明确告诉 MyBatis-Plus 该字段不是数据库表中的列
    private String username; // 账号（登录名）
    /**
     * 上传该文件的用户真实姓名
     * 该字段不存入数据库，用于在查询时返回用户信息
     */
    @TableField(exist = false)
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
     * 该字段不存入数据库，用于在查询时返回用户权限信息
     */
    @TableField(exist = false)
    private Integer currentUserPermission;
}