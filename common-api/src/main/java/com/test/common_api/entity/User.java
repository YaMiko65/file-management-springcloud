package com.test.common_api.entity;

import lombok.Data;

import java.util.Date;

/**
 * 用户实体类
 */
@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String name; // 用户姓名
    private Integer role; // 0-普通用户，1-管理员
    private Date createTime;
    private Date updateTime;
}