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

@Service
public class BatchOperationService {

    @Autowired
    private FileClient fileClient;

    @Autowired
    private UserClient userClient;

    /**
     * 处理Excel批量操作
     * 修复缺陷6：增加管理员权限验证
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
     */
    private boolean isAdmin(Long userId) {
        if (userId == null) {
            return false;
        }
        User user = userClient.getById(userId);
        // 用户存在且角色为1（管理员）
        return user != null && user.getRole() != null && user.getRole() == 1;
    }

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