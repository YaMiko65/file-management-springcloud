package com.test.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.test.common_api.client.FileClient;
import com.test.common_api.client.UserClient;
import com.test.common_api.entity.ExcelData;
import com.test.common_api.entity.Folder;
import com.test.common_api.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 批量操作服务
 * 处理从Excel文件中批量导入的权限分配等操作
 * 包括批量创建文件夹、用户和分配权限的功能
 */
@Service
public class BatchOperationService {

    @Autowired
    private FileClient fileClient;

    @Autowired
    private UserClient userClient;

    /**
     * 处理Excel批量操作
     * 修复缺陷6：增加管理员权限验证
     * 遍历Excel数据，批量创建文件夹、用户并分配权限
     * 如果操作用户不是管理员，则返回全0结果
     *
     * @param dataList 从Excel解析出的数据列表
     * @param adminId 执行操作的管理员ID
     * @return 操作结果统计，包含文件夹、用户和权限的创建数量
     */
    @Transactional
    public Map<String, Integer> processExcelData(List<ExcelData> dataList, Long adminId) {
        // ------------------ 修复缺陷6：增加权限校验 ------------------
        if (!isAdmin(adminId)) {
            // 如果不是管理员，返回全0结果或抛出异常
            Map<String, Integer> emptyResult = new HashMap<>();
            emptyResult.put("folderCount", 0);
            emptyResult.put("userCount", 0);
            emptyResult.put("permissionCount", 0);
            return emptyResult;
        }
        // -----------------------------------------------------------

        Map<String, Integer> result = new HashMap<>();
        int folderCount = 0;
        int userCount = 0;
        int permissionCount = 0;

        for (ExcelData data : dataList) {
            // 验证数据完整性 (使用你项目中ExcelData实际存在的get方法)
            if (data.getFolderName() == null || data.getUsername() == null || data.getPermission() == null) {
                continue;
            }

            // 1. 创建文件夹（如果不存在）
            Folder folder = getOrCreateFolder(data.getFolderName(), adminId);
            if (folder != null) {
                // 注意：这里简单统计，只要返回非空就算成功，实际逻辑可根据需求调整
                folderCount++;
            }

            // 2. 创建用户（如果不存在）
            User user = getOrCreateUser(data.getUsername(), data.getName());
            if (user != null) {
                userCount++;
            }

            // 3. 分配权限
            if (folder != null && user != null) {
                boolean permResult = fileClient.grantPermission(
                        folder.getId(), user.getId(), data.getPermission());
                if (permResult) {
                    permissionCount++;
                }
            }
        }

        result.put("folderCount", folderCount);
        result.put("userCount", userCount);
        result.put("permissionCount", permissionCount);

        return result;
    }

    /**
     * 辅助方法：验证用户是否为管理员
     * 通过查询用户信息并检查角色字段来验证用户是否为管理员
     *
     * @param userId 用户ID
     * @return 是否为管理员
     */
    private boolean isAdmin(Long userId) {
        if (userId == null) {
            return false;
        }
        User user = userClient.getById(userId);
        // 用户存在且角色为1（管理员）
        return user != null && user.getRole() != null && user.getRole() == 1;
    }

    /**
     * 获取或创建文件夹
     * 如果文件夹已存在则返回现有文件夹，否则创建新的文件夹
     * 首先查询是否存在同名且同创建者的文件夹，如果不存在则创建
     *
     * @param folderName 文件夹名称
     * @param adminId 管理员ID
     * @return 文件夹对象
     */
    // 获取或创建文件夹
    private Folder getOrCreateFolder(String folderName, Long adminId) {
        // 检查文件夹是否已存在
        List<Folder> existingFolders = fileClient.list(
                new QueryWrapper<Folder>()
                        .eq("name", folderName)
                        .eq("creator_id", adminId)
        );

        if (!existingFolders.isEmpty()) {
            return existingFolders.get(0);
        }

        // 创建新文件夹
        boolean created = fileClient.createFolder(folderName, adminId);
        if (created) {
            // 创建成功后重新查询以获取ID
            List<Folder> newFolders = fileClient.list(
                    new QueryWrapper<Folder>()
                            .eq("name", folderName)
                            .eq("creator_id", adminId)
            );
            return newFolders.isEmpty() ? null : newFolders.get(0);
        }

        return null;
    }

    /**
     * 获取或创建用户
     * 如果用户已存在则返回现有用户，否则创建新的用户
     * 首先通过用户名查询用户，如果不存在则使用用户名+123作为默认密码创建新用户
     *
     * @param username 用户名
     * @param name 用户真实姓名
     * @return 用户对象
     */
    // 获取或创建用户
    private User getOrCreateUser(String username, String name) {
        User existingUser = userClient.findByUsername(username);
        if (existingUser != null) {
            return existingUser;
        }

        // 创建新用户，设置默认密码 (用户名 + "123")
        // 注意：这里需要UserClient支持createUser方法，如果之前修复时添加了该方法则没问题
        boolean created = userClient.createUser(username, name, username + "123");
        return created ? userClient.findByUsername(username) : null;
    }
}