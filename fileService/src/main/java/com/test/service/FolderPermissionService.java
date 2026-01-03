package com.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.test.entity.FolderPermission;

import java.util.List;

/**
     * 文件夹权限服务接口
     * 定义文件夹权限的授予、查询、删除等核心功能
     */
    public interface FolderPermissionService extends IService<FolderPermission> {
    /**
     * 授予用户文件夹权限
     * 为指定用户授予对指定文件夹的访问权限
     *
     * @param folderId 文件夹ID
     * @param userId 用户ID
     * @param permission 权限级别（1-只读，2-读写，3-管理）
     * @return 授权是否成功
     */
    boolean grantPermission(Long folderId, Long userId, Integer permission);
    /**
     * 查询文件夹的所有权限
     * 获取指定文件夹的所有用户权限记录
     *
     * @param folderId 文件夹ID
     * @return 文件夹权限列表
     */
    List<FolderPermission> getPermissionsByFolder(Long folderId);
    /**
     * 检查用户对文件夹的权限
     * 检查指定用户对指定文件夹的权限级别
     *
     * @param userId 用户ID
     * @param folderId 文件夹ID
     * @return 权限级别（1-只读，2-读写，3-管理），无权限则返回null
     */
    Integer checkPermission(Long userId, Long folderId);
    /**
     * 根据用户ID删除所有相关的文件夹权限
     * 删除指定用户的所有文件夹权限记录
     *
     * @param userId 用户ID
     * @return 删除是否成功
     */
    boolean deleteByUserId(Long userId);

    /**
     * 删除用户对文件夹的权限
     * 删除指定用户对指定文件夹的权限记录
     *
     * @param folderId 文件夹ID
     * @param userId 用户ID
     * @return 删除是否成功
     */
    boolean deletePermission(Long folderId, Long userId);

    /**
     * 根据文件夹ID删除所有权限
     * 删除指定文件夹的所有用户权限记录（用于删除文件夹时级联清理）
     *
     * @param folderId 文件夹ID
     * @return 删除是否成功
     */
    boolean deleteByFolderId(Long folderId);
}