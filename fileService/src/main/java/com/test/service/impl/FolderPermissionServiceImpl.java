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

@Service
public class FolderPermissionServiceImpl extends ServiceImpl<FolderPermissionMapper, FolderPermission> implements FolderPermissionService {

    @Autowired
    private FolderPermissionMapper permissionMapper;

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

    @Override
    @Transactional
    public List<FolderPermission> getPermissionsByFolder(Long folderId) {
        return permissionMapper.selectByFolderId(folderId);
    }

    @Override
    @Transactional
    public Integer checkPermission(Long userId, Long folderId) {
        FolderPermission permission = permissionMapper.selectByUserIdAndFolderId(userId, folderId);
        return permission != null ? permission.getPermission() : 0;
    }

    @Override
    @Transactional
    public boolean deleteByUserId(Long userId) {
        QueryWrapper<FolderPermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        baseMapper.delete(queryWrapper);
        return true;
    }

    @Override
    @Transactional
    public boolean deletePermission(Long folderId, Long userId) {
        QueryWrapper<FolderPermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("folder_id", folderId)
                .eq("user_id", userId);
        return remove(queryWrapper);
    }

    // 新增实现
    @Override
    @Transactional
    public boolean deleteByFolderId(Long folderId) {
        QueryWrapper<FolderPermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("folder_id", folderId);
        return remove(queryWrapper);
    }
}