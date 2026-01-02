package com.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.entity.FolderPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FolderPermissionMapper extends BaseMapper<FolderPermission> {

    @Select("select * from folder_permission where folder_id = #{folderId}")
    List<FolderPermission> selectByFolderId(Long folderId);

    // 根据用户ID和文件夹ID查询权限
    @Select("select * from folder_permission where user_id = #{userId} and folder_id = #{folderId}")
    FolderPermission selectByUserIdAndFolderId(@Param("userId") Long userId,@Param("folderId") Long folderId);
}