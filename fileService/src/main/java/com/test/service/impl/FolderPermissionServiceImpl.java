package com.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.entity.FolderPermission;
import com.test.mapper.FolderPermissionMapper;
import com.test.service.FolderPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 文件夹权限服务实现类
 * 实现文件夹权限的授予、查询、删除等核心功能
 */
@Service
public class FolderPermissionServiceImpl extends ServiceImpl<FolderPermissionMapper, FolderPermission> implements FolderPermissionService {

    @Autowired
    private FolderPermissionMapper permissionMapper;

    /**
     * 授予用户对文件夹的权限
     * 如果用户已存在权限记录，则更新权限；否则创建新的权限记录
     * 使用更新或新增逻辑，避免先删后增导致的权限空窗期
     *
     * @param folderId 文件夹ID
     * @param userId 用户ID
     * @param permission 权限级别（1-只读，2-读写，3-管理）
     * @return 授权是否成功
     */
    @Override
    @Transactional
    public boolean grantPermission(Long folderId, Long userId, Integer permission) {
        // 修复缺陷7：使用更新或新增逻辑，避免先删后增导致的权限空窗期
        FolderPermission existing = permissionMapper.selectByUserIdAndFolderId(userId, folderId);

        if (existing != null) {
            existing.setPermission(permission);
            existing.setUpdateTime(new Date());
            return updateById(existing);
        } else {
            FolderPermission newPermission = new FolderPermission();
            newPermission.setFolderId(folderId);
            newPermission.setUserId(userId);
            newPermission.setPermission(permission);
            newPermission.setCreateTime(new Date());
            newPermission.setUpdateTime(new Date());
            return save(newPermission);
        }
    }

    /**
     * 获取文件夹的权限列表
     * 获取指定文件夹的所有用户权限记录
     *
     * @param folderId 文件夹ID
     * @return 文件夹权限列表
     */
    @Override
    @Transactional
    public List<FolderPermission> getPermissionsByFolder(Long folderId) {
        return permissionMapper.selectByFolderId(folderId);
    }

    /**
     * 检查用户对文件夹的权限
     * 检查指定用户对指定文件夹的权限级别
     * 通过查询数据库中用户与文件夹的权限记录来确定权限级别
     *
     * @param userId 用户ID
     * @param folderId 文件夹ID
     * @return 权限级别（1-只读，2-读写，3-管理），无权限则返回0
     */
    @Override
    @Transactional
    public Integer checkPermission(Long userId, Long folderId) {
        FolderPermission permission = permissionMapper.selectByUserIdAndFolderId(userId, folderId);
        return permission != null ? permission.getPermission() : 0;
    }

    /**
     * 根据用户ID删除权限记录
     * 删除指定用户的所有文件夹权限记录
     *
     * @param userId 用户ID
     * @return 删除是否成功
     */
    @Override
    @Transactional
    public boolean deleteByUserId(Long userId) {
        QueryWrapper<FolderPermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        baseMapper.delete(queryWrapper);
        return true;
    }

    /**
     * 删除用户对文件夹的权限
     * 删除指定用户对指定文件夹的权限记录
     *
     * @param folderId 文件夹ID
     * @param userId 用户ID
     * @return 删除是否成功
     */
    @Override
    @Transactional
    public boolean deletePermission(Long folderId, Long userId) {
        QueryWrapper<FolderPermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("folder_id", folderId)
                .eq("user_id", userId);
        return remove(queryWrapper);
    }

    /**
     * 根据文件夹ID删除权限记录
     * 删除指定文件夹的所有用户权限记录
     *
     * @param folderId 文件夹ID
     * @return 删除是否成功
     */
    // 新增实现
    @Override
    @Transactional
    public boolean deleteByFolderId(Long folderId) {
        QueryWrapper<FolderPermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("folder_id", folderId);
        return remove(queryWrapper);
    }
}