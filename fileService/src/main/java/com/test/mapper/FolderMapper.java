package com.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.test.entity.Folder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FolderMapper extends BaseMapper<Folder> {
    // 根据实际表结构和字段名调整SQL
    @Select("select * from folder where creator_id = #{creatorId} order by create_time desc")
    List<Folder> selectByCreatorId(Long creatorId);

    // 支持分页的查询方法
    @Select("select * from folder where creator_id = #{creatorId} order by create_time desc")
    IPage<Folder> selectByCreatorIdWithPage(@Param("page") Page<Folder> page,@Param("creatorId") Long creatorId);
}