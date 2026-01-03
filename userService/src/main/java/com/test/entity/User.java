package com.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 用户实体类
 * 用于表示系统中的用户信息
 */
@Data
@TableName("user")
public class User {
    /**
     * 用户唯一标识ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户登录账号
     */
    private String username;
    /**
     * 用户登录密码
     */
    private String password;
    /**
     * 用户真实姓名
     */
    private String name; // 用户姓名
    /**
     * 用户角色类型：0-普通用户，1-管理员
     */
    private Integer role; // 0-普通用户，1-管理员
    /**
     * 用户创建时间
     */
    private Date createTime;
    /**
     * 用户信息更新时间
     */
    private Date updateTime;
}