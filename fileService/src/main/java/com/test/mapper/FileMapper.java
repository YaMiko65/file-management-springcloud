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
     * 根据用户ID查询文件（包含自己上传的文件和有权限查看的文件）
     * 修复逻辑：
     * 1. 关联 folder_permission 表查询权限。
     * 2. 使用 CASE WHEN 逻辑计算当前用户的权限：如果是文件所有者，权限为3（管理）；否则取授权的权限值。
     * 3. 将结果映射别名为 current_user_permission，以便 MyBatis 自动填充到 File 实体的 currentUserPermission 字段。
     *
     * @param userId 用户ID
     * @return 文件列表
     */
    @Select("select distinct f.*, " +
            "CASE WHEN f.user_id = #{userId} THEN 3 ELSE fp.permission END as current_user_permission " +
            "from file f " +
            "left join folder_permission fp on f.folder_id = fp.folder_id and fp.user_id = #{userId} " +
            "where f.user_id = #{userId} or fp.id is not null " +
            "order by f.create_time desc")
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