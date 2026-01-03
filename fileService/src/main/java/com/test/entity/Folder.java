/**
 * 文件夹实体类
 * 用于表示系统中的文件夹信息
 */
package com.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("folder")
public class Folder {
    /**
     * 文件夹唯一标识ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 文件夹名称
     */
    private String name; // 文件夹名称
    /**
     * 文件夹创建者ID（通常是管理员）
     */
    private Long creatorId; // 创建者ID（管理员）
    /**
     * 文件夹创建时间
     */
    private Date createTime;
    /**
     * 文件夹信息更新时间
     */
    private Date updateTime;
}