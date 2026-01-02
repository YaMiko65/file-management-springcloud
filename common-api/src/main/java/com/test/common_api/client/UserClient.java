package com.test.common_api.client;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.test.common_api.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "userService")
public interface UserClient {
    @GetMapping("/login/{username}/{password}")
    public User login(@PathVariable("username") String username,@PathVariable("password") String password);
    @PostMapping("/register")
    public boolean register(@RequestBody User user);
    @GetMapping("/updateUserPassword/{userId}/{newPassword}")
    public boolean updateUserPassword(@PathVariable("userId") Long userId, @PathVariable("newPassword") String newPassword);
    @PostMapping("/admin/users/searchUsers")
    public Page<User> searchUsers(@RequestBody Page<User> page,@RequestParam("keyword") String keyword);

    @PostMapping("/admin/users/getAllUsersPage")
    public Page<User> getAllUsersPage(@RequestBody Page<User> page);
    @GetMapping("/admin/users/updateUserRole/{userId}/{role}")
    public boolean updateUserRole(@PathVariable("userId") Long userId,@PathVariable("role") Integer role);
    @GetMapping("/admin/users/getById/{userId}")
    public User getById(@PathVariable("userId") Long userId);
    @GetMapping("/admin/users/updateUserName/{userId}/{newName}")
    public boolean updateUserName(@PathVariable("userId") Long userId,@PathVariable("newName") String newName);
    @GetMapping("/admin/users/list")
    public List<User> list();
    @GetMapping("/admin/users/removeById/{userId}")
    public boolean removeById(@PathVariable("userId") Long userId);
    @GetMapping("/admin/users/findByUsername/{username}")
    public User findByUsername(@PathVariable("username") String username);
    @GetMapping("/admin/users/createUser/{username}/{name}/{defaultPassword}")
    public boolean createUser(
            @PathVariable("username") String username,
            @PathVariable("name") String name,
            @PathVariable("defaultPassword") String defaultPassword
    );

}
