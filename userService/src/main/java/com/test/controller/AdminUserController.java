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


    @PostMapping("/searchUsers")
    public Page<User> searchUsers(@RequestBody Page<User> page,@RequestParam("keyword") String keyword){
        log.debug("searchUsers:获得的参数：page:{},keyword:{}",page,keyword);
        IPage<User> userPage = userService.searchUsers(page, keyword);
        log.debug("userService.searchUsers:{}",userPage);
        return (Page<User>) userPage;
    }
    @PostMapping("/getAllUsersPage")
    public Page<User> getAllUsersPage(@RequestBody Page<User> page){
        log.debug("sgetAllUsersPage:获得的参数：page:{}",page);
        IPage<User> userPage = userService.getAllUsersPage(page);
        log.debug("userService.getAllUsersPage:{}",userPage);
        return (Page<User>) userPage;
    }
    @GetMapping("/updateUserRole/{userId}/{role}")
    public boolean updateUserRole(@PathVariable("userId") Long userId,@PathVariable("role") Integer role){
        log.debug("updateUserRole:获得的参数：userId:{},role:{}",userId,role);
        boolean b = userService.updateUserRole(userId, role);
        log.debug("userService.updateUserRole:{}",b);
        return b;
    }
    @GetMapping("/getById/{userId}")
    @Transactional
    public User getById(@PathVariable("userId") Long userId){
        log.debug("getById:获得的参数：userId:{}",userId);
        User user = userService.getById(userId);
        log.debug("userService.getById:{}",user);
        return user;
    }
    @GetMapping("/updateUserName/{userId}/{newName}")
    public boolean updateUserName(@PathVariable("userId") Long userId,@PathVariable("newName") String newName){
        log.debug("updateUserName:获得的参数：userId:{},newName:{}",userId,newName);
        boolean b = userService.updateUserName(userId, newName);
        log.debug("userService.updateUserName:{}",b);
        return b;
    }
    @GetMapping("/list")
    public List<User> list(){
        List<User> list = userService.list();
        log.debug("userService.list:{}",list);
        return list;
    }
    @GetMapping("/removeById/{userId}")
    public boolean removeById(@PathVariable("userId") Long userId){
        log.debug("removeById:获得的参数：userId:{}",userId);
        boolean b = userService.removeById(userId);
        log.debug("userService.removeById:{}",b);
        return b;
    }
    @GetMapping("/findByUsername/{username}")
    public User findByUsername(@PathVariable("username") String username){
        log.debug("findByUsername:获得的参数：username:{}",username);
        User user = userService.findByUsername(username);
        log.debug("userService.findByUsername:{}",username);
        return user;
    }
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