package com.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.entity.File;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文件Mapper接口
 */
@Mapper
public interface FileMapper extends BaseMapper<File> {

    /**
     * 根据用户ID查询文件
     * @param userId 用户ID
     * @return 文件列表
     */
    @Select("select * from file where user_id = #{userId} order by create_time desc")
    List<File> selectByUserId(Long userId);

    /**
     * 查询所有文件
     * @return 文件列表
     */
    @Select("select * from file order by create_time desc")
    List<File> selectAll();

    /**
     * 根据文件夹ID统计文件数量
     * @param folderId 文件夹ID
     * @return 文件数量
     */
    @Select("select count(*) from file where folder_id = #{folderId}")
    int countByFolderId(Long folderId);

    /**
     * 根据文件夹ID查询文件
     * @param folderId 文件夹ID
     * @return 文件列表
     */
    @Select("select * from file where folder_id = #{folderId} order by create_time desc")
    List<File> selectByFolderId(Long folderId);
}