package com.test.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.test.entity.User;
import com.test.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService;

    /**
     * 搜索用户（分页）
     * 根据关键词搜索用户名或姓名匹配的用户
     *
     * @param page 分页对象
     * @param keyword 搜索关键词
     * @return 分页的用户列表
     */
    @PostMapping("/searchUsers")
    public Page<User> searchUsers(@RequestBody Page<User> page,@RequestParam("keyword") String keyword){
        log.debug("searchUsers:获得的参数：page:{},keyword:{}",page,keyword);
        IPage<User> userPage = userService.searchUsers(page, keyword);
        log.debug("userService.searchUsers:{}",userPage);
        return (Page<User>) userPage;
    }

    /**
     * 获取所有用户（分页）
     *
     * @param page 分页对象
     * @return 分页的用户列表
     */
    @PostMapping("/getAllUsersPage")
    public Page<User> getAllUsersPage(@RequestBody Page<User> page){
        log.debug("sgetAllUsersPage:获得的参数：page:{}",page);
        IPage<User> userPage = userService.getAllUsersPage(page);
        log.debug("userService.getAllUsersPage:{}",userPage);
        return (Page<User>) userPage;
    }

    /**
     * 更新用户角色
     *
     * @param userId 用户ID
     * @param role 新角色（0-普通用户，1-管理员）
     * @return 更新是否成功
     */
    @GetMapping("/updateUserRole/{userId}/{role}")
    public boolean updateUserRole(@PathVariable("userId") Long userId,@PathVariable("role") Integer role){
        log.debug("updateUserRole:获得的参数：userId:{},role:{}",userId,role);
        boolean b = userService.updateUserRole(userId, role);
        log.debug("userService.updateUserRole:{}",b);
        return b;
    }

    /**
     * 根据ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户对象
     */
    @GetMapping("/getById/{userId}")
    @Transactional
    public User getById(@PathVariable("userId") Long userId){
        log.debug("getById:获得的参数：userId:{}",userId);
        User user = userService.getById(userId);
        log.debug("userService.getById:{}",user);
        return user;
    }

    /**
     * 更新用户姓名
     *
     * @param userId 用户ID
     * @param newName 新姓名
     * @return 更新是否成功
     */
    @GetMapping("/updateUserName/{userId}/{newName}")
    public boolean updateUserName(@PathVariable("userId") Long userId,@PathVariable("newName") String newName){
        log.debug("updateUserName:获得的参数：userId:{},newName:{}",userId,newName);
        boolean b = userService.updateUserName(userId, newName);
        log.debug("userService.updateUserName:{}",b);
        return b;
    }

    /**
     * 获取所有用户列表
     *
     * @return 用户列表
     */
    @GetMapping("/list")
    public List<User> list(){
        List<User> list = userService.list();
        log.debug("userService.list:{}",list);
        return list;
    }

    /**
     * 根据ID删除用户
     *
     * @param userId 用户ID
     * @return 删除是否成功
     */
    @GetMapping("/removeById/{userId}")
    public boolean removeById(@PathVariable("userId") Long userId){
        log.debug("removeById:获得的参数：userId:{}",userId);
        boolean b = userService.removeById(userId);
        log.debug("userService.removeById:{}",b);
        return b;
    }

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    @GetMapping("/findByUsername/{username}")
    public User findByUsername(@PathVariable("username") String username){
        log.debug("findByUsername:获得的参数：username:{}",username);
        User user = userService.findByUsername(username);
        log.debug("userService.findByUsername:{}",username);
        return user;
    }

    /**
     * 创建新用户
     *
     * @param username 用户名
     * @param name 用户真实姓名
     * @param defaultPassword 默认密码
     * @return 创建是否成功
     */
    @GetMapping("/createUser/{username}/{name}/{defaultPassword}")
    public boolean createUser(
            @PathVariable("username") String username,
            @PathVariable("name") String name,
            @PathVariable("defaultPassword") String defaultPassword
            ){
        log.debug("createUser:获得的参数：username:{},name:{},defaultPassword",username,name,defaultPassword);
        boolean user = userService.createUser(username, name, defaultPassword);
        log.debug("userService.createUser:{}",user);
        return user;
    }
}