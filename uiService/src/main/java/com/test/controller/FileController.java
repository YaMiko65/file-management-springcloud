package com.test.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.test.common_api.client.*;
import com.test.common_api.entity.File;
import com.test.common_api.entity.Folder;
import com.test.common_api.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 文件控制器
 */
@Controller
@CrossOrigin
public class FileController {
    
    @Autowired
    private FileClient fileClient;
    @Autowired
    private OperationLogClient operationLogClient;


    @Autowired
    private UserClient userClient;
    
    @Value("${file.upload-path}")
    private String uploadPath;

    
    /**
     * 管理员文件列表
     */
    @GetMapping("/admin/files")
    public String allFileList(
            @RequestParam(required = false) Long folderId,
            @RequestParam(defaultValue = "1") Integer pageNum,  // 新增：当前页码，默认1
            @RequestParam(defaultValue = "10") Integer pageSize, // 新增：每页条数，默认10
            Model model,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != 1) {
            return "redirect:/login";
        }

        // 创建分页对象
        Page<File> page = new Page<>(pageNum, pageSize);
        IPage<File> filePage;
        // 分页查询文件
        if (folderId != null) {
            filePage = fileClient.getByFolderId(page, folderId);
        } else {
            filePage = fileClient.getAll(page);
        }

        User users = null;
        // 为每个文件添加用户名
        for (File file : filePage.getRecords()) {
       // 同时设置用户名和姓名
            users = userClient.getById(file.getUserId());
            if (users != null) {
                file.setUsername(users.getUsername()); // 用户名（登录名）
                file.setUserRealName(users.getName()); // 用户姓名
            }
        }

        List<Folder> allFolders = fileClient.list();
        model.addAttribute("files", filePage.getRecords());
        model.addAttribute("user", user);
        model.addAttribute("allFolders", allFolders);
        model.addAttribute("selectedFolderId", folderId);

        // 添加分页信息到模型
        model.addAttribute("currentPage", filePage.getCurrent());
        model.addAttribute("totalPages", filePage.getPages());
        model.addAttribute("totalItems", filePage.getTotal());
        model.addAttribute("pageSize", filePage.getSize());

        return "admin/files";
    }
    
    /**
     * 上传文件
     */
    @PostMapping("/files/upload")
    public String upload(@RequestParam("file") MultipartFile file,
                         @RequestParam("folderId") Long folderId, // 新增：接收文件夹ID
                         HttpSession session,
                         HttpServletRequest request,
                         Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // 普通用户需校验文件夹权限（管理员不受限）
        if (user.getRole() != 1) { // 1为管理员角色
            Integer permission = fileClient.checkPermission(user.getId(), folderId);
            // 权限为0（无权限）或1（只读）时，禁止上传
            if (permission == 0 || permission == 1) {
                model.addAttribute("error", "无权限上传文件到该文件夹（需读写或管理权限）");
                return fileList(model, session); // 返回文件列表页并显示错误
            }
        }

        if (file.isEmpty()) {
            model.addAttribute("error", "请选择要上传的文件");
            return fileList(model, session);
        }

        String ipAddress = getIpAddress(request);
        // 上传时传入folderId，关联文件与文件夹
        List<MultipartFile> fileList = new StandardMultipartHttpServletRequest(request).getFiles("file");
        boolean result = fileClient.upload(fileList, user.getId(), folderId, ipAddress);

        if (result) {
            model.addAttribute("message", "文件上传成功");
        } else {
            model.addAttribute("error", "文件上传失败");
        }

        return "redirect:/files";
    }

    // 用户文件列表页加载时，查询其有权限的文件夹
    @GetMapping("/files")
    public String fileList(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<File> files = fileClient.getByUserId(user.getId());
        model.addAttribute("files", files);
        model.addAttribute("user", user);

        // 普通用户：查询有读写/管理权限的文件夹（用于上传时选择）
        if (user.getRole() != 1) {
            List<Folder> authorizedFolders = fileClient.getAuthorizedFolders(user.getId());
            model.addAttribute("authorizedFolders", authorizedFolders);
        } else {
            // 管理员：显示所有文件夹
            List<Folder> allFolders = fileClient.list();
            model.addAttribute("authorizedFolders", allFolders);
        }

        return "files";
    }
    
    /**
     * 下载文件
     */
    @GetMapping("/files/download/{id}")
    public void download(@PathVariable("id") Long id,
                        HttpServletResponse response,
                        HttpSession session,
                        HttpServletRequest request) throws IOException {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("/login");
            return;
        }
        
        File file = fileClient.getById(id);
        if (file == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "文件不存在");
            return;
        }
        
        // 检查权限（只有文件所有者或管理员可以下载文件）
        if (!file.getUserId().equals(user.getId()) && user.getRole() != 1) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "无权下载此文件");
            return;
        }
        
        // 记录下载日志
        String ipAddress = getIpAddress(request);
        operationLogClient.recordLog(user.getId(), file.getId(), "download", ipAddress);
        
        // 设置响应头
        response.setContentType(file.getFileType());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getFileName() + "\"");
        
        // 读取文件并写入响应
        Path filePath = Paths.get(file.getFilePath());
        Files.copy(filePath, response.getOutputStream());
    }
    
    /**
     * 删除文件
     */
    @GetMapping("/files/delete/{id}")
    public String delete(@PathVariable("id") Long id,
                        HttpSession session,
                        Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        File file = fileClient.getById(id);
        if (file == null) {
            model.addAttribute("error", "文件不存在");
        } else {
            // 检查权限（只有文件所有者或管理员可以删除文件）
            if (!file.getUserId().equals(user.getId()) && user.getRole() != 1) {
                model.addAttribute("error", "无权删除此文件");
            } else {
                boolean result = fileClient.delete(id, user.getId());
                if (result) {
                    model.addAttribute("message", "文件删除成功");
                } else {
                    model.addAttribute("error", "文件删除失败");
                }
            }
        }
        
        // 根据用户角色返回不同页面
        if (user.getRole() == 1) {
            return "redirect:/admin/files";
        } else {
            return "redirect:/files";
        }
    }
    
    /**
     * 获取IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}