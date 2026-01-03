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

/**
 * 文件夹服务实现类
 * 实现文件夹的创建、删除、查询等核心功能
 */
@Service
public class FolderServiceImpl extends ServiceImpl<FolderMapper, Folder> implements FolderService {

    @Autowired
    private FolderMapper folderMapper;
    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private FolderPermissionService folderPermissionService;

    /**
     * 创建文件夹
     * 为指定管理员创建一个新的文件夹
     *
     * @param folderName 文件夹名称
     * @param adminId 管理员ID
     * @return 创建是否成功
     */
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

    /**
     * 获取管理员创建的文件夹列表
     *
     * @param adminId 管理员ID
     * @return 管理员创建的文件夹列表
     */
    @Override
    @Transactional
    public List<Folder> getFoldersByAdmin(Long adminId) {
        return folderMapper.selectByCreatorId(adminId);
    }

    /**
     * 删除文件夹
     * 删除指定ID的文件夹，但仅当该文件夹为空且为当前管理员创建时才允许删除
     *
     * @param folderId 文件夹ID
     * @param adminId 管理员ID
     * @return 删除是否成功
     */
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

    /**
     * 获取用户有权限访问的文件夹列表
     * 获取用户拥有读写或管理权限的文件夹列表
     *
     * @param userId 用户ID
     * @return 用户有权限访问的文件夹列表
     */
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

    /**
     * 获取管理员创建的文件夹列表（分页）
     *
     * @param page 分页对象
     * @param adminId 管理员ID
     * @return 分页的文件夹列表
     */
    @Override
    @Transactional
    public IPage<Folder> getFoldersByAdmin(Page<Folder> page, Long adminId) {
        return folderMapper.selectByCreatorIdWithPage(page, adminId);
    }

    /**
     * 检查并删除某管理员创建的所有文件夹
     * 在删除管理员前，先检查并删除该管理员创建的所有文件夹
     *
     * @param adminId 管理员ID
     * @return 删除是否成功
     */
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