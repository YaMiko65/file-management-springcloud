package com.test.controller;

import com.test.entity.User;
import com.test.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * 提供用户登录、注册、密码修改等REST API接口
 */
@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * 验证用户名和密码是否匹配
     *
     * @param username 用户名
     * @param password 用户密码
     * @return 登录成功的用户对象，验证失败则返回null
     */
    @GetMapping("/login/{username}/{password}")
    public User login(@PathVariable("username") String username,@PathVariable("password") String password){
        log.debug("/login接收到的username:{},password:{}",username,password);
        User user = userService.login(username, password);
        log.debug("user:{}",user);
        return user;
    }

    /**
     * 用户注册
     * 注册新用户账户
     *
     * @param user 用户对象
     * @return 注册是否成功
     */
    @PostMapping("/register")
    public boolean register(@RequestBody User user){
        boolean register = userService.register(user);
        log.debug("userService.register:{}",register);
        return register;
    }

    /**
     * 更新用户密码
     *
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 更新是否成功
     */
    @GetMapping("/updateUserPassword/{userId}/{newPassword}")
    public boolean updateUserPassword(@PathVariable("userId") Long userId, @PathVariable("newPassword") String newPassword){
        boolean b = userService.updateUserPassword(userId, newPassword);
        log.debug("userService.updateUserPassword:{}",b);
        return b;
    }

}