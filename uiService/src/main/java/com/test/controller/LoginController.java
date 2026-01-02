package com.test.controller;

import com.test.common_api.client.UserClient;
import com.test.common_api.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/**
 * 登录控制器
 */
@Controller
@Slf4j
@CrossOrigin
public class LoginController {
    
    @Autowired
    private UserClient userClient;
    
    /**
     * 登录页面
     */
    @GetMapping("/login")
    public String loginPage() {
        log.debug("/login被访问了");
        return "login";
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                       @RequestParam("password") String password,
                       Model model,
                       HttpSession session) {
        User user = userClient.login(username, password);
        if (user != null) {
            // 登录成功，将用户信息存入session
            session.setAttribute("user", user);
            // 根据角色跳转到不同页面
            if (user.getRole() == 1) {
                return "redirect:/admin/files";
            } else {
                return "redirect:/files";
            }
        } else {
            // 登录失败，返回错误信息
            model.addAttribute("error", "用户名或密码错误");
            return "login";
        }
    }
    
    /**
     * 用户注册页面
     */
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public String register(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("name") String name, // 接收姓名
            Model model) {
        // 验证密码
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "两次输入的密码不一致");
            return "register";
        }

        // 创建用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setName(name); // 设置姓名

        // 注册用户
        boolean result = userClient.register(user);
        if (result) {
            model.addAttribute("message", "注册成功，请登录");
            return "login";
        } else {
            model.addAttribute("error", "教职工号已存在");
            return "register";
        }
    }
    
    /**
     * 用户退出
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}