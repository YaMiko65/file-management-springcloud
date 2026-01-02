package com.test.controller;

import com.test.entity.User;
import com.test.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/login/{username}/{password}")
    public User login(@PathVariable("username") String username,@PathVariable("password") String password){
        log.debug("/login接收到的username:{},password:{}",username,password);
        User user = userService.login(username, password);
        log.debug("user:{}",user);
        return user;
    }

    @PostMapping("/register")
    public boolean register(@RequestBody User user){
        boolean register = userService.register(user);
        log.debug("userService.register:{}",register);
        return register;
    }
    @GetMapping("/updateUserPassword/{userId}/{newPassword}")
    public boolean updateUserPassword(@PathVariable("userId") Long userId, @PathVariable("newPassword") String newPassword){
        boolean b = userService.updateUserPassword(userId, newPassword);
        log.debug("userService.updateUserPassword:{}",b);
        return b;
    }

}