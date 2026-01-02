// src/main/java/com/example/filemanagement/service/BatchOperationService.java
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


    // 处理Excel批量操作
    @Transactional
    public Map<String, Integer> processExcelData(List<ExcelData> dataList, Long adminId) {
        Map<String, Integer> result = new HashMap<>();
        int folderCount = 0;
        int userCount = 0;
        int permissionCount = 0;

        for (ExcelData data : dataList) {
            // 验证数据
            if (data.getFolderName() == null || data.getUsername() == null || data.getPermission() == null) {
                continue;
            }

            // 1. 创建文件夹（如果不存在）
            Folder folder = getOrCreateFolder(data.getFolderName(), adminId);
            if (folder != null) {
                folderCount++;
            }

            // 2. 创建用户（如果不存在）
            User user = getOrCreateUser(data.getUsername(), data.getName()); // 传入姓名
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
    private User getOrCreateUser(String username, String name) { // 新增name参数
        User existingUser = userClient.findByUsername(username);
        if (existingUser != null) {
            return existingUser;
        }

        // 创建新用户，包含姓名
        boolean created = userClient.createUser(username, name, username + "123");
        return created ? userClient.findByUsername(username) : null;
    }
}