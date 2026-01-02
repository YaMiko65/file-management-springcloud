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
    private FileMapper fileMapper;

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
        int fileCount = fileMapper.countByFolderId(folderId);
        if (fileCount > 0) {
            return false; // 文件夹非空，不允许删除
        }

        // 修复：删除文件夹前，先删除关联的权限记录，防止脏数据
        folderPermissionService.deleteByFolderId(folderId);

        return removeById(folderId);
    }

    @Override
    @Transactional
    public List<Folder> getAuthorizedFolders(Long userId) {
        List<FolderPermission> permissions = folderPermissionService.list(
                new QueryWrapper<FolderPermission>()
                        .eq("user_id", userId)
                        .ge("permission", 2)
        );
        List<Long> folderIds = permissions.stream()
                .map(FolderPermission::getFolderId)
                .collect(Collectors.toList());
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

    // 新增方法的实现
    @Override
    @Transactional
    public boolean checkAndDeleteByCreatorId(Long adminId) {
        // 1. 获取该管理员创建的所有文件夹
        List<Folder> folders = folderMapper.selectByCreatorId(adminId);

        if (folders == null || folders.isEmpty()) {
            return true;
        }

        // 2. 预检查：是否有非空文件夹
        for (Folder folder : folders) {
            int fileCount = fileMapper.countByFolderId(folder.getId());
            if (fileCount > 0) {
                // 存在非空文件夹，禁止删除管理员
                return false;
            }
        }

        // 3. 执行删除：全是空文件夹，安全删除
        for (Folder folder : folders) {
            // 先删权限
            folderPermissionService.deleteByFolderId(folder.getId());
            // 再删文件夹
            removeById(folder.getId());
        }

        return true;
    }
}