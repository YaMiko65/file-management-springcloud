package com.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.common_api.client.FileClient;
import com.test.common_api.client.OperationLogClient;
import com.test.common_api.entity.File;
import com.test.common_api.entity.OperationLog;
import com.test.entity.User;
import com.test.mapper.UserMapper;
import com.test.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FileClient fileClient;

    @Autowired
    private OperationLogClient operationLogClient;

    @Override
    @Transactional
    public User login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    @Override
    @Transactional
    public User getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    @Transactional
    public String getUsernameById(Long userId) {
        User user = getById(userId);
        return user != null ? user.getUsername() : "未知用户";
    }

    @Override
    @Transactional
    public List<User> getAllUsers() {
        return userMapper.selectAll();
    }

    @Override
    @Transactional
    public boolean updateUserRole(Long userId, Integer role) {
        // 角色合法性校验
        if (role != 0 && role != 1) {
            return false;
        }

        // 防止删除最后一个管理员
        if (role == 0) {
            int adminCount = (int) count(new QueryWrapper<User>().eq("role", 1));
            if (adminCount <= 1) {
                return false; // 只剩最后一个管理员，不允许修改
            }
        }

        User user = new User();
        user.setId(userId);
        user.setRole(role);
        user.setUpdateTime(new Date());
        return updateById(user);
    }

    @Override
    @Transactional // 事务保证数据一致性
    public boolean deleteUser(Long userId) {
        // 修复：删除用户前，先检查该用户（如果是管理员）是否创建了文件夹
        // 如果有非空文件夹，checkAndDeleteByCreator 会返回 false，此时禁止删除用户
        boolean canDelete = fileClient.checkAndDeleteByCreator(userId);
        if (!canDelete) {
            // 表示有非空文件夹，无法删除
            return false;
        }

        // 1. 删除用户的文件夹权限记录
        fileClient.deleteFolderByUserId(userId);

        // 2. 删除用户关联的文件
        // 修复：调用批量删除接口，避免遍历调用单删接口导致的权限模拟问题
        fileClient.deleteByUserId(userId);

        // 3. 删除用户的操作日志（该用户产生的操作记录）
        operationLogClient.deleteByUserId(userId);

        // 4. 删除用户本身
        return removeById(userId);
    }

    @Override
    @Transactional
    public IPage<User> getAllUsersPage(Page<User> page) {
        return userMapper.selectPage(page, null);
    }

    @Override
    @Transactional
    public IPage<User> searchUsers(Page<User> page, String keyword) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("username", keyword)
                .or()
                .like("name", keyword);
        return userMapper.selectPage(page, queryWrapper);
    }

    @Override
    @Transactional
    public boolean updateUserPassword(Long userId, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            return false;
        }
        user.setPassword(newPassword);
        user.setUpdateTime(new Date());
        return updateById(user);
    }

    @Override
    @Transactional
    public User findByUsername(String username) {
        return getOne(new QueryWrapper<User>().eq("username", username));
    }

    @Override
    @Transactional
    public boolean createUser(String username, String name, String defaultPassword) {
        // 检查用户名是否已存在
        if (findByUsername(username) != null) {
            return false;
        }

        User user = new User();
        user.setUsername(username);
        user.setName(name); // 设置姓名
        user.setPassword(defaultPassword); // 注意：实际应用中需要加密
        user.setRole(0); // 默认普通用户
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        return save(user);
    }

    /**
     * 更新用户姓名
     */
    @Override
    @Transactional
    public boolean updateUserName(Long userId, String newName) {
        if (userId == null || newName == null || newName.trim().isEmpty()) {
            return false;
        }
        User user = new User();
        user.setId(userId);
        user.setName(newName.trim());
        user.setUpdateTime(new Date());
        return updateById(user);
    }

    // 支持注册时传入姓名
    @Override
    @Transactional
    public boolean register(User user) {
        // 检查用户名是否已存在
        User existingUser = userMapper.selectByUsername(user.getUsername());
        if (existingUser != null) {
            return false;
        }

        // 设置默认角色为普通用户
        user.setRole(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        return save(user); // 自动包含name字段
    }
}