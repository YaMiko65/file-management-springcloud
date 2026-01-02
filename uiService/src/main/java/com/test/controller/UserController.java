package com.test.controller;

import com.test.common_api.client.UserClient;
import com.test.common_api.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserClient userClient;

    // 显示用户自己的密码修改页面
    @GetMapping("/editPassword")
    public String showUserEditPasswordPage(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login"; // 未登录则跳转登录页
        }
        return "user/edit-password";
    }

    // 处理用户自己的密码修改提交
    @PostMapping("/updatePassword")
    public String updateOwnPassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session,
            Model model) {

        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        // 验证原密码是否正确
        if (!currentUser.getPassword().equals(oldPassword)) {
            model.addAttribute("error", "原密码错误");
            return "user/edit-password";
        }

        // 验证新密码一致性
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "两次输入的新密码不一致");
            return "user/edit-password";
        }

        // 调用服务层更新密码
        boolean success = userClient.updateUserPassword(currentUser.getId(), newPassword);
        if (success) {
            // 更新session中的用户密码（可选，避免后续操作密码不一致）
            currentUser.setPassword(newPassword);
            session.setAttribute("user", currentUser);
            model.addAttribute("message", "密码修改成功，请重新登录");
            return "login"; // 密码修改后建议重新登录
        } else {
            model.addAttribute("error", "密码修改失败，请重试");
            return "user/edit-password";
        }
    }
}