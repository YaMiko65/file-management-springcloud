package com.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.test.entity.FolderPermission;

import java.util.List;

public interface FolderPermissionService extends IService<FolderPermission> {
    // 授予用户文件夹权限
    boolean grantPermission(Long folderId, Long userId, Integer permission);
    // 查询文件夹的所有权限
    List<FolderPermission> getPermissionsByFolder(Long folderId);
    // 检查用户对文件夹的权限
    Integer checkPermission(Long userId, Long folderId);
    /**
     * 根据用户ID删除所有相关的文件夹权限
     * @param userId 用户ID
     */
    boolean deleteByUserId(Long userId);

    boolean deletePermission(Long folderId, Long userId);

    // 新增：根据文件夹ID删除所有权限（用于删除文件夹时级联清理）
    boolean deleteByFolderId(Long folderId);
}