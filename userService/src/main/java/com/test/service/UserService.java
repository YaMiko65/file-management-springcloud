package com.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.test.entity.User;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    User login(String username, String password);
    /**
     * 用户注册
     * @param user 用户信息
     * @return 是否注册成功
     */
    boolean register(User user);
    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户信息
     */
    User getByUsername(String username);
    /**
     * 根据用户ID获取用户名
     * @param userId 用户ID
     * @return 用户名（不存在返回null）
     */
    String getUsernameById(Long userId);
    /**
     * 获取所有用户
     */
    List<User> getAllUsers();
    /**
     * 更新用户角色
     */
    boolean updateUserRole(Long userId, Integer role);
    /**
     * 删除用户（含关联数据）
     */
    boolean deleteUser(Long userId);
    // 新增分页方法
    IPage<User> getAllUsersPage(Page<User> page);
    // 修改用户密码
    boolean updateUserPassword(Long userId, String newPassword);

    User findByUsername(String username);

    /**
     * 创建用户
     */
    boolean createUser(String username, String name, String defaultPassword);

    /**
     * 更新用户姓名
     */
    boolean updateUserName(Long userId, String newName);

    IPage<User> searchUsers(Page<User> page, String keyword);

}