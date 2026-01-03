package com.test.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.test.common_api.client.BatchOperationClient;
import com.test.common_api.client.FileClient;
import com.test.common_api.client.UserClient;
import com.test.common_api.entity.ExcelData;
import com.test.common_api.entity.Folder;
import com.test.common_api.entity.FolderPermission;
import com.test.common_api.entity.User;
import com.test.common_api.utils.ExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 管理员文件夹管理
 * 处理管理员对文件夹和权限管理的UI请求
 */

@Controller
@RequestMapping("/admin/folders")
@CrossOrigin
public class AdminFolderController {

    @Autowired
    private FileClient fileClient;

    @Autowired
    private BatchOperationClient batchOperationClient;
    @Autowired
    private UserClient userClient;

    /**
     * 文件夹管理首页
     * 显示管理员创建的文件夹列表，支持分页
     *
     * @param pageNum 页码，默认为1
     * @param pageSize 页面大小，默认为10
     * @param session HTTP会话
     * @param model Spring MVC模型
     * @return 文件夹列表页面
     */
    // 文件夹管理首页
    @GetMapping
    public String folderList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpSession session,
            Model model) {
        User admin = (User) session.getAttribute("user");
        if (admin == null || admin.getRole() != 1) {
            return "redirect:/login";
        }

        // 创建分页对象
        Page<Folder> page = new Page<>(pageNum, pageSize);
        // 调用分页查询方法
        IPage<Folder> folderPage = fileClient.getFoldersByAdmin(page, admin.getId());

        model.addAttribute("folders", folderPage.getRecords());
        model.addAttribute("user", admin);

        // 添加分页信息
        model.addAttribute("currentPage", folderPage.getCurrent());
        model.addAttribute("totalPages", folderPage.getPages());
        model.addAttribute("totalItems", folderPage.getTotal());
        model.addAttribute("pageSize", folderPage.getSize());

        return "admin/folders";
    }

    /**
     * 创建文件夹
     * 处理管理员创建新文件夹的请求
     *
     * @param folderName 文件夹名称
     * @param session HTTP会话
     * @param model Spring MVC模型
     * @return 重定向到文件夹列表页面
     */
    // 创建文件夹
    @PostMapping("/create")
    public String createFolder(@RequestParam("folderName") String folderName,
                               HttpSession session, Model model) {
        User admin = (User) session.getAttribute("user");
        if (admin == null || admin.getRole() != 1) {
            return "redirect:/login";
        }
        boolean success = fileClient.createFolder(folderName, admin.getId());
        if (success) {
            model.addAttribute("message", "文件夹创建成功");
        } else {
            model.addAttribute("error", "文件夹创建失败");
        }
        return "redirect:/admin/folders";
    }

    /**
     * 权限管理页面
     * 显示文件夹权限列表并提供授予权限功能
     *
     * @param folderId 文件夹ID
     * @param session HTTP会话
     * @param model Spring MVC模型
     * @return 权限管理页面
     */
    // 权限管理页面（显示文件夹权限并授予权限）
    @GetMapping("/permissions/{folderId}")
    public String managePermissions(@PathVariable("folderId") Long folderId,
                                    HttpSession session, Model model) {
        User admin = (User) session.getAttribute("user");
        if (admin == null || admin.getRole() != 1) {
            return "redirect:/login";
        }
        // 验证文件夹是否为当前管理员创建
        Folder folder = fileClient.getFolderById(folderId);
        if (folder == null || !folder.getCreatorId().equals(admin.getId())) {
            model.addAttribute("error", "无权操作此文件夹");
            return "redirect:/admin/folders";
        }
        // 获取文件夹现有权限
        List<FolderPermission> permissions = fileClient.getPermissionsByFolder(folderId);

        // 为每个权限记录查询并设置用户名
        for (FolderPermission permission : permissions) {
            User user = userClient.getById(permission.getUserId());
            if (user != null) {
                permission.setUsername(user.getUsername());
                permission.setUserFullName(user.getName());
            }
        }

        // 获取所有用户（供授权选择）
        List<User> allUsers = userClient.list();
        model.addAttribute("folder", folder);
        model.addAttribute("permissions", permissions);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("user", admin);
        return "admin/folder-permissions";
    }

    /**
     * 授予权限
     * 为用户授予对文件夹的访问权限
     *
     * @param folderId 文件夹ID
     * @param userId 用户ID
     * @param permission 权限级别（1-只读，2-读写，3-管理）
     * @param session HTTP会话
     * @param model Spring MVC模型
     * @return 重定向到权限管理页面
     */
    // 授予权限
    @PostMapping("/permissions/grant")
    public String grantPermission(@RequestParam("folderId") Long folderId,
                                  @RequestParam("userId") Long userId,
                                  @RequestParam("permission") Integer permission,
                                  HttpSession session, Model model) {
        User admin = (User) session.getAttribute("user");
        if (admin == null || admin.getRole() != 1) {
            return "redirect:/login";
        }
        boolean success = fileClient.grantPermission(folderId, userId, permission);
        if (success) {
            model.addAttribute("message", "权限授予成功");
        } else {
            model.addAttribute("error", "权限授予失败");
        }
        return "redirect:/admin/folders/permissions/" + folderId;
    }

    /**
     * 删除文件夹
     * 删除指定ID的文件夹（仅当文件夹为空时才允许删除）
     *
     * @param folderId 文件夹ID
     * @param session HTTP会话
     * @param model Spring MVC模型
     * @return 重定向到文件夹列表页面
     */
    // 删除文件夹
    @GetMapping("/delete/{folderId}")
    public String deleteFolder(@PathVariable("folderId") Long folderId,
                               HttpSession session, Model model) {
        User admin = (User) session.getAttribute("user");
        if (admin == null || admin.getRole() != 1) {
            return "redirect:/login";
        }
        boolean success = fileClient.deleteFolder(folderId, admin.getId());
        if (success) {
            model.addAttribute("message", "文件夹删除成功");
        } else {
            model.addAttribute("error", "文件夹删除失败（可能包含文件）");
        }
        return "redirect:/admin/folders";
    }

    /**
     * 处理权限修改
     * 更新用户对文件夹的访问权限
     *
     * @param folderId 文件夹ID
     * @param userId 用户ID
     * @param permission 权限级别（1-只读，2-读写，3-管理）
     * @param session HTTP会话
     * @param model Spring MVC模型
     * @return 重定向到权限管理页面
     */
    // 处理权限修改
    @PostMapping("/permissions/update")
    public String updatePermission(@RequestParam("folderId") Long folderId,
                                   @RequestParam("userId") Long userId,
                                   @RequestParam("permission") Integer permission,
                                   HttpSession session, Model model) {
        User admin = (User) session.getAttribute("user");
        if (admin == null || admin.getRole() != 1) {
            return "redirect:/login";
        }
        // 复用grantPermission方法（内部会先删除旧权限再添加新权限）
        boolean success = fileClient.grantPermission(folderId, userId, permission);
        if (success) {
            model.addAttribute("message", "权限修改成功");
        } else {
            model.addAttribute("error", "权限修改失败");
        }
        return "redirect:/admin/folders/permissions/" + folderId;
    }

    /**
     * 显示批量上传页面
     * 显示用于批量处理权限分配的Excel上传页面
     *
     * @param session HTTP会话
     * @param model Spring MVC模型
     * @return 批量上传页面
     */
    @GetMapping("/batch-upload")
    public String showBatchUploadPage(HttpSession session, Model model) {
        User admin = (User) session.getAttribute("user");
        if (admin == null || admin.getRole() != 1) {
            return "redirect:/login";
        }
        model.addAttribute("user", admin);
        return "admin/batch-upload";
    }

    /**
     * 处理批量上传
     * 解析上传的Excel文件并批量处理权限分配
     *
     * @param file 上传的Excel文件
     * @param session HTTP会话
     * @param model Spring MVC模型
     * @return 批量上传页面，显示处理结果
     */
    @PostMapping("/batch-process")
    public String processBatchUpload(
            @RequestParam("file") MultipartFile file,
            HttpSession session,
            Model model) {

        User admin = (User) session.getAttribute("user");
        if (admin == null || admin.getRole() != 1) {
            return "redirect:/login";
        }

        // 无论处理结果如何，都将用户对象添加到模型
        model.addAttribute("user", admin);

        if (file.isEmpty()) {
            model.addAttribute("error", "请选择Excel文件");
            return "admin/batch-upload";
        }

        try {
            // 解析Excel
            List<ExcelData> dataList = ExcelUtils.parseExcel(file);
            // 处理数据
            Map<String, Integer> result = batchOperationClient.processExcelData(dataList, admin.getId());

            model.addAttribute("message", String.format(
                    "处理完成：创建文件夹%d个，创建用户%d个，分配权限%d个",
                    result.get("folderCount"),
                    result.get("userCount"),
                    result.get("permissionCount")
            ));
        } catch (Exception e) {
            model.addAttribute("error", "处理失败：" + e.getMessage());
            e.printStackTrace();
        }

        return "admin/batch-upload";
    }

    /**
     * 删除权限
     * 撤销用户对文件夹的访问权限
     *
     * @param folderId 文件夹ID
     * @param userId 用户ID
     * @param session HTTP会话
     * @param model Spring MVC模型
     * @return 重定向到权限管理页面
     */
    @PostMapping("/permissions/delete")
    public String deletePermission(
            @RequestParam("folderId") Long folderId,
            @RequestParam("userId") Long userId,
            HttpSession session,
            Model model) {
        User admin = (User) session.getAttribute("user");
        if (admin == null || admin.getRole() != 1) {
            return "redirect:/login";
        }
        // 调用权限服务删除权限
        boolean success = fileClient.deletePermission(folderId, userId);
        if (success) {
            model.addAttribute("message", "权限已成功撤销");
        } else {
            model.addAttribute("error", "权限撤销失败");
        }
        return "redirect:/admin/folders/permissions/" + folderId;
    }
}