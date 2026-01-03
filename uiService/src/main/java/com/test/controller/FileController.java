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
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // [新增引用]

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
 * 处理文件上传、下载、删除等UI相关的请求
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
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Model model,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != 1) {
            return "redirect:/login";
        }

        Page<File> page = new Page<>(pageNum, pageSize);
        IPage<File> filePage;
        if (folderId != null) {
            filePage = fileClient.getByFolderId(page, folderId);
        } else {
            filePage = fileClient.getAll(page);
        }

        for (File file : filePage.getRecords()) {
            User u = userClient.getById(file.getUserId());
            if (u != null) {
                file.setUsername(u.getUsername());
                file.setUserRealName(u.getName());
            }
        }

        List<Folder> allFolders = fileClient.list();
        model.addAttribute("files", filePage.getRecords());
        model.addAttribute("user", user);
        model.addAttribute("allFolders", allFolders);
        model.addAttribute("selectedFolderId", folderId);

        model.addAttribute("currentPage", filePage.getCurrent());
        model.addAttribute("totalPages", filePage.getPages());
        model.addAttribute("totalItems", filePage.getTotal());
        model.addAttribute("pageSize", filePage.getSize());

        return "admin/files";
    }

    /**
     * 上传文件
     * [修改] 增加 RedirectAttributes 用于重定向传参
     */
    @PostMapping("/files/upload")
    public String upload(@RequestParam("file") MultipartFile file,
                         @RequestParam("folderId") Long folderId,
                         HttpSession session,
                         HttpServletRequest request,
                         Model model,
                         RedirectAttributes redirectAttributes) { // [修改]
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        if (user.getRole() != 1) {
            Integer permission = fileClient.checkPermission(user.getId(), folderId);
            if (permission == null || permission < 2) {
                model.addAttribute("error", "无权限上传文件到该文件夹（需读写或管理权限）");
                return fileList(model, session);
            }
        }

        if (file.isEmpty()) {
            model.addAttribute("error", "请选择要上传的文件");
            return fileList(model, session);
        }

        String ipAddress = getIpAddress(request);
        List<MultipartFile> fileList = new StandardMultipartHttpServletRequest(request).getFiles("file");
        boolean result = fileClient.upload(fileList, user.getId(), folderId, ipAddress);

        // [修改] 使用 flashAttribute 在重定向后保留消息
        if (result) {
            redirectAttributes.addFlashAttribute("message", "文件上传成功");
        } else {
            redirectAttributes.addFlashAttribute("error", "文件上传失败");
        }

        return "redirect:/files";
    }

    /**
     * 更新文件 (重新上传)
     * [修改] 增加 RedirectAttributes 用于重定向传参
     */
    @PostMapping("/files/update")
    public String update(@RequestParam("fileId") Long fileId,
                         @RequestParam("file") MultipartFile file,
                         HttpSession session,
                         HttpServletRequest request,
                         Model model,
                         RedirectAttributes redirectAttributes) { // [修改]
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        if (file.isEmpty()) {
            model.addAttribute("error", "请选择新的文件");
            return fileList(model, session);
        }

        File existingFile = fileClient.getById(fileId);
        if (existingFile == null) {
            model.addAttribute("error", "文件不存在");
            return fileList(model, session);
        }

        boolean isOwner = existingFile.getUserId().equals(user.getId());
        boolean isAdmin = user.getRole() == 1;
        Integer permission = fileClient.checkPermission(user.getId(), existingFile.getFolderId());
        boolean hasWritePerm = (permission != null && permission >= 2);

        if (!isOwner && !isAdmin && !hasWritePerm) {
            model.addAttribute("error", "无权修改此文件");
            return fileList(model, session);
        }

        String ipAddress = getIpAddress(request);
        boolean result = fileClient.update(fileId, file, user.getId(), ipAddress);

        // [修改] 使用 flashAttribute 在重定向后保留消息
        if (result) {
            redirectAttributes.addFlashAttribute("message", "文件修改成功");
        } else {
            redirectAttributes.addFlashAttribute("error", "文件修改失败(请检查后端日志，可能是存储路径错误)");
        }

        return "redirect:/files";
    }

    /**
     * 用户文件列表页
     */
    @GetMapping("/files")
    public String fileList(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<File> files = fileClient.getAccessibleFiles(user.getId());

        for(File f : files) {
            User u = userClient.getById(f.getUserId());
            if(u != null) {
                f.setUserRealName(u.getName());
            }
        }

        model.addAttribute("files", files);
        model.addAttribute("user", user);

        if (user.getRole() != 1) {
            List<Folder> authorizedFolders = fileClient.getAuthorizedFolders(user.getId());
            model.addAttribute("authorizedFolders", authorizedFolders);
        } else {
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

        Integer permission = fileClient.checkPermission(user.getId(), file.getFolderId());
        if (user.getRole() != 1 && (permission == null || permission < 1)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "无权下载此文件");
            return;
        }

        String ipAddress = getIpAddress(request);
        operationLogClient.recordLog(user.getId(), file.getId(), "download", ipAddress);

        response.setContentType(file.getFileType());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getFileName() + "\"");

        Path filePath = Paths.get(file.getFilePath());
        if (Files.exists(filePath)) {
            Files.copy(filePath, response.getOutputStream());
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "物理文件丢失");
        }
    }

    /**
     * 删除文件
     * [修改] 增加 RedirectAttributes 用于重定向传参
     */
    @GetMapping("/files/delete/{id}")
    public String delete(@PathVariable("id") Long id,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) { // [修改]
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        File file = fileClient.getById(id);
        String redirectUrl = (user.getRole() == 1) ? "redirect:/admin/files" : "redirect:/files";

        if (file == null) {
            redirectAttributes.addFlashAttribute("error", "文件不存在"); // [修改]
            return redirectUrl;
        }

        Integer permission = fileClient.checkPermission(user.getId(), file.getFolderId());
        boolean canDelete = user.getRole() == 1 || (permission != null && permission >= 2);

        if (!canDelete) {
            redirectAttributes.addFlashAttribute("error", "无权删除此文件（需读写或管理权限）"); // [修改]
        } else {
            boolean result = fileClient.delete(id, user.getId());
            if (result) {
                redirectAttributes.addFlashAttribute("message", "文件删除成功"); // [修改]
            } else {
                redirectAttributes.addFlashAttribute("error", "文件删除失败"); // [修改]
            }
        }

        return redirectUrl;
    }

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