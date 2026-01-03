package com.test.common_api.client;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.test.common_api.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户服务客户端接口
 * 提供与其他服务通信的用户相关API接口
 */
@FeignClient(name = "userService")
public interface UserClient {
    /**
     * 用户登录验证
     *
     * @param username 用户名
     * @param password 用户密码
     * @return 用户对象，如果验证失败则返回null
     */
    @GetMapping("/login/{username}/{password}")
    public User login(@PathVariable("username") String username,@PathVariable("password") String password);
    
    /**
     * 用户注册
     *
     * @param user 用户对象
     * @return 注册是否成功
     */
    @PostMapping("/register")
    public boolean register(@RequestBody User user);
    
    /**
     * 更新用户密码
     *
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 更新是否成功
     */
    @GetMapping("/updateUserPassword/{userId}/{newPassword}")
    public boolean updateUserPassword(@PathVariable("userId") Long userId, @PathVariable("newPassword") String newPassword);
    
    /**
     * 搜索用户（分页）
     *
     * @param page 分页对象
     * @param keyword 搜索关键词
     * @return 分页的用户列表
     */
    @PostMapping("/admin/users/searchUsers")
    public Page<User> searchUsers(@RequestBody Page<User> page,@RequestParam("keyword") String keyword);

    /**
     * 获取所有用户（分页）
     *
     * @param page 分页对象
     * @return 分页的用户列表
     */
    @PostMapping("/admin/users/getAllUsersPage")
    public Page<User> getAllUsersPage(@RequestBody Page<User> page);
    
    /**
     * 更新用户角色
     *
     * @param userId 用户ID
     * @param role 新角色（0-普通用户，1-管理员）
     * @return 更新是否成功
     */
    @GetMapping("/admin/users/updateUserRole/{userId}/{role}")
    public boolean updateUserRole(@PathVariable("userId") Long userId,@PathVariable("role") Integer role);
    
    /**
     * 根据ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户对象
     */
    @GetMapping("/admin/users/getById/{userId}")
    public User getById(@PathVariable("userId") Long userId);
    
    /**
     * 更新用户姓名
     *
     * @param userId 用户ID
     * @param newName 新姓名
     * @return 更新是否成功
     */
    @GetMapping("/admin/users/updateUserName/{userId}/{newName}")
    public boolean updateUserName(@PathVariable("userId") Long userId,@PathVariable("newName") String newName);
    
    /**
     * 获取所有用户列表
     *
     * @return 用户列表
     */
    @GetMapping("/admin/users/list")
    public List<User> list();
    
    /**
     * 根据ID删除用户
     *
     * @param userId 用户ID
     * @return 删除是否成功
     */
    @GetMapping("/admin/users/removeById/{userId}")
    public boolean removeById(@PathVariable("userId") Long userId);
    
    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    @GetMapping("/admin/users/findByUsername/{username}")
    public User findByUsername(@PathVariable("username") String username);
    
    /**
     * 创建新用户
     *
     * @param username 用户名
     * @param name 用户真实姓名
     * @param defaultPassword 默认密码
     * @return 创建是否成功
     */
    @GetMapping("/admin/users/createUser/{username}/{name}/{defaultPassword}")
    public boolean createUser(
            @PathVariable("username") String username,
            @PathVariable("name") String name,
            @PathVariable("defaultPassword") String defaultPassword
    );

}
