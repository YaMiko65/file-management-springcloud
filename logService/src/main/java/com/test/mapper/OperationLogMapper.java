package com.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.test.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 操作日志Mapper接口
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
    
    /**
     * 根据用户ID查询操作日志
     * @param userId 用户ID
     * @return 操作日志列表
     */
    @Select("select * from operation_log where user_id = #{userId} order by operation_time desc")
    List<OperationLog> selectByUserId(Long userId);
    
    /**
     * 查询所有操作日志
     * @return 操作日志列表
     */
    @Select("select * from operation_log order by operation_time desc")
    List<OperationLog> selectAll();

    // 添加分页查询方法
    @Select("select * from operation_log order by operation_time desc")
    IPage<OperationLog> selectOperationLogPage(@Param("page") Page<OperationLog> page);
}