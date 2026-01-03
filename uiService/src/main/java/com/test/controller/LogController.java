package com.test.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.test.common_api.client.FileClient;
import com.test.common_api.client.OperationLogClient;
import com.test.common_api.client.UserClient;
import com.test.common_api.entity.File;
import com.test.common_api.entity.OperationLog;
import com.test.common_api.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/**
 * 操作日志控制器
 * 处理操作日志管理的UI请求
 */
@Controller
@CrossOrigin
public class LogController {
    
    @Autowired
    private OperationLogClient operationLogClient;
    @Autowired
    private UserClient userClient;
    @Autowired // 注入文件服务
    private FileClient fileClient;
    
    /**
     * 操作日志列表（仅管理员可访问）
     * 显示系统中的操作日志，支持分页
     *
     * @param pageNum 页码，默认为1
     * @param pageSize 页面大小，默认为10
     * @param model Spring MVC模型
     * @param session HTTP会话
     * @return 操作日志列表页面
     */
    /**
     * 操作日志列表（仅管理员可访问）
     */
    @GetMapping("/admin/logs")
    public String logList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Model model,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != 1) {
            return "redirect:/login";
        }

        // 创建分页对象并查询
        Page<OperationLog> page = new Page<>(pageNum, pageSize);
        IPage<OperationLog> logPage = operationLogClient.getAll(page);

        // 为每个日志添加用户名和文件名
        for (OperationLog log : logPage.getRecords()) {
            // 设置用户名
            User users = userClient.getById(log.getUserId());
            if (users != null) {
                log.setUsername(users.getUsername()); // 用户名（登录名）
                log.setUserRealName(users.getName()); // 用户姓名
            }

            // 设置文件名
            File file = fileClient.getById(log.getFileId());
            log.setFileName(file != null ? file.getFileName() : "未知文件（已删除）");
        }

        model.addAttribute("logs", logPage.getRecords());
        model.addAttribute("user", user);

        // 添加分页信息
        model.addAttribute("currentPage", logPage.getCurrent());
        model.addAttribute("totalPages", logPage.getPages());
        model.addAttribute("totalItems", logPage.getTotal());
        model.addAttribute("pageSize", logPage.getSize());

        return "admin/logs";
    }
}