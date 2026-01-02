package com.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.test.entity.Folder;

import java.util.List;

public interface FolderService extends IService<Folder> {
    // 创建文件夹（管理员）
    boolean createFolder(String folderName, Long adminId);
    // 管理员查询自己创建的文件夹
    List<Folder> getFoldersByAdmin(Long adminId);
    // 分页查询管理员创建的文件夹
    IPage<Folder> getFoldersByAdmin(Page<Folder> page, Long adminId);
    // 删除文件夹（需先检查是否有文件）
    boolean deleteFolder(Long folderId, Long adminId);
    // 查询用户有读写/管理权限的文件夹
    List<Folder> getAuthorizedFolders(Long userId);
}