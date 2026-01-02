package com.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.test.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    @Select("select * from user where username = #{username}")
    User selectByUsername(String username);

    /**
     * 根据用户ID查询用户名
     * @param userId 用户ID
     * @return 用户名
     */
    @Select("select username from user where id = #{userId}")
    String selectUsernameById(Long userId);

    /**
     * 查询所有用户
     */
    @Select("select * from user")
    List<User> selectAll();

    // 新增：分页查询所有用户
    @Select("select * from user order by create_time desc")
    IPage<User> selectUserPage(@Param("page") Page<User> page);

    
}