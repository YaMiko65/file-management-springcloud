package com.test.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.test.common_api.client.FileClient;
import com.test.common_api.client.OperationLogClient;
import com.test.common_api.client.UserClient;
import com.test.common_api.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/users")
@CrossOrigin
public class AdminUserController {

    @Autowired
    private UserClient userClient;


    @Autowired
    private OperationLogClient operationLogClient;

    @Autowired
    private FileClient fileClient;

    /**
     * 显示用户列表（支持搜索）
     */
    @GetMapping
    public String userList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            HttpSession session,
            Model model) {
        User admin = (User) session.getAttribute("user");
        if (admin == null || admin.getRole() != 1) {
            return "redirect:/login";
        }

        // 创建分页对象并查询
        Page<User> page = new Page<>(pageNum, pageSize);
        IPage<User> userPage;

        if (keyword != null && !keyword.isEmpty()) {
            // 按用户名或姓名搜索
            userPage = userClient.searchUsers(page, keyword);
        } else {
            userPage = userClient.getAllUsersPage(page);
        }

        model.addAttribute("users", userPage.getRecords());
        model.addAttribute("user", admin);
        model.addAttribute("keyword", keyword); // 回显搜索关键词

        // 添加分页信息
        model.addAttribute("currentPage", userPage.getCurrent());
        model.addAttribute("totalPages", userPage.getPages());
        model.addAttribute("totalItems", userPage.getTotal());
        model.addAttribute("pageSize", userPage.getSize());

        return "admin/users";
    }

    /**
     * 更新用户角色
     */
    @PostMapping("/updateRole")
    public String updateRole(@RequestParam Long userId,
                             @RequestParam Integer role,
                             Model model) {
        boolean success = userClient.updateUserRole(userId, role);
        if (success) {
            model.addAttribute("message", "角色更新成功");
        } else {
            model.addAttribute("error", "角色更新失败（可能是最后一个管理员）");
        }
        return "redirect:/admin/users";
    }

    /**
     * 显示修改姓名页面
     */
    @GetMapping("/edit-name/{userId}")
    public String showEditNamePage(
            @PathVariable Long userId,
            HttpSession session,
            Model model) {

        User admin = (User) session.getAttribute("user");
        if (admin == null || admin.getRole() != 1) {
            return "redirect:/login";
        }

        User user = userClient.getById(userId);
        if (user == null) {
            model.addAttribute("error", "用户不存在");
            return "redirect:/admin/users";
        }

        model.addAttribute("userToEdit", user);
        model.addAttribute("admin", admin);
        return "admin/edit-user-name";
    }

    /**
     * 处理姓名修改提交
     */
    @PostMapping("/update-name")
    public String updateUserName(
            @RequestParam Long userId,
            @RequestParam String newName,
            HttpSession session,
            Model model) {

        User admin = (User) session.getAttribute("user");
        if (admin == null || admin.getRole() != 1) {
            return "redirect:/login";
        }

        if (newName == null || newName.trim().isEmpty()) {
            model.addAttribute("error", "姓名不能为空");
            model.addAttribute("userToEdit", userClient.getById(userId));
            return "admin/edit-user-name";
        }

        boolean success = userClient.updateUserName(userId, newName);
        if (success) {
            model.addAttribute("message", "姓名修改成功");
        } else {
            model.addAttribute("error", "姓名修改失败");
        }

        return "redirect:/admin/users";
    }

    /**
     * 删除用户
     */
    @GetMapping("/delete/{userId}")
    public String deleteUser(
            @PathVariable Long userId,
            HttpSession session,
            Model model) {

        User admin = (User) session.getAttribute("user");
        if (admin == null || admin.getRole() != 1) {
            return "redirect:/login";
        }

        // 禁止删除管理员自己
        if (admin.getId().equals(userId)) {
            model.addAttribute("error", "不能删除当前登录的管理员账号");
            return "redirect:/admin/users";
        }

        // 1. 删除用户的文件夹权限记录
        fileClient.deleteFolderByUserId(userId);
        // 2. 删除用户的操作日志记录（新增）
        operationLogClient.deleteByUserId(userId);
        // 3. 删除用户的文件记录（如果需要）
        fileClient.deleteByUserId(userId);
        // 4. 最后删除用户
        boolean success = userClient.removeById(userId);

        if (success) {
            model.addAttribute("message", "用户删除成功");
        } else {
            model.addAttribute("error", "用户删除失败");
        }

        return "redirect:/admin/users";
    }

    // 显示修改密码页面
    @GetMapping("/editPassword/{userId}")
    public String showEditPasswordPage(@PathVariable Long userId, HttpSession session, Model model) {
        User admin = (User) session.getAttribute("user");
        if (admin == null || admin.getRole() != 1) {
            return "redirect:/login";
        }
        User user = userClient.getById(userId);
        model.addAttribute("user", user);
        return "admin/edit-password";
    }


    // 处理修改密码提交
    @PostMapping("/updatePassword")
    public String updatePassword(@RequestParam Long userId,
                                 @RequestParam String newPassword,
                                 Model model) {
        boolean success = userClient.updateUserPassword(userId, newPassword);
        if (success) {
            model.addAttribute("message", "密码修改成功");
        } else {
            model.addAttribute("error", "密码修改失败");
        }
        return "redirect:/admin/users";
    }
}