package com.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.entity.Folder;
import com.test.entity.FolderPermission;
import com.test.mapper.FileMapper;
import com.test.mapper.FolderMapper;
import com.test.service.FolderPermissionService;
import com.test.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FolderServiceImpl extends ServiceImpl<FolderMapper, Folder> implements FolderService {

    @Autowired
    private FolderMapper folderMapper;
    @Autowired
    private FileMapper fileMapper; // 用于检查文件夹是否有文件

    @Autowired
    private FolderPermissionService folderPermissionService;

    @Override
    @Transactional
    public boolean createFolder(String folderName, Long adminId) {
        Folder folder = new Folder();
        folder.setName(folderName);
        folder.setCreatorId(adminId);
        folder.setCreateTime(new Date());
        folder.setUpdateTime(new Date());
        return save(folder);
    }

    @Override
    @Transactional
    public List<Folder> getFoldersByAdmin(Long adminId) {
        return folderMapper.selectByCreatorId(adminId);
    }

    @Override
    @Transactional
    public boolean deleteFolder(Long folderId, Long adminId) {
        // 校验文件夹是否存在且为当前管理员创建
        Folder folder = getById(folderId);
        if (folder == null || !folder.getCreatorId().equals(adminId)) {
            return false;
        }
        // 检查文件夹下是否有文件
        int fileCount = fileMapper.countByFolderId(folderId); // 需在FileMapper中新增此方法
        if (fileCount > 0) {
            return false; // 文件夹非空，不允许删除
        }
        return removeById(folderId);
    }

    @Override
    @Transactional
    public List<Folder> getAuthorizedFolders(Long userId) {
        // 查询用户所有权限记录（权限≥2）
        List<FolderPermission> permissions = folderPermissionService.list(
                new QueryWrapper<FolderPermission>()
                        .eq("user_id", userId)
                        .ge("permission", 2) // 只保留读写（2）和管理（3）权限
        );
        // 提取文件夹ID列表
        List<Long> folderIds = permissions.stream()
                .map(FolderPermission::getFolderId)
                .collect(Collectors.toList());
        // 查询对应的文件夹
        if (folderIds.isEmpty()) {
            return new ArrayList<>();
        }
        return list(new QueryWrapper<Folder>().in("id", folderIds));
    }

    @Override
    @Transactional
    public IPage<Folder> getFoldersByAdmin(Page<Folder> page, Long adminId) {
        return folderMapper.selectByCreatorIdWithPage(page, adminId);
    }
}