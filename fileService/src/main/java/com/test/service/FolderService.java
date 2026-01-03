package com.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.test.entity.Folder;

import java.util.List;

/**
     * 文件夹服务接口
     * 定义文件夹的创建、删除、查询等核心功能
     */
    public interface FolderService extends IService<Folder> {
    /**
     * 创建文件夹
     * 为指定管理员创建一个新的文件夹
     *
     * @param folderName 文件夹名称
     * @param adminId 管理员ID
     * @return 创建是否成功
     */
    boolean createFolder(String folderName, Long adminId);
    /**
     * 管理员查询自己创建的文件夹
     * 获取指定管理员创建的所有文件夹列表
     *
     * @param adminId 管理员ID
     * @return 文件夹列表
     */
    List<Folder> getFoldersByAdmin(Long adminId);
    /**
     * 分页查询管理员创建的文件夹
     * 获取指定管理员创建的文件夹列表，并支持分页显示
     *
     * @param page 分页对象
     * @param adminId 管理员ID
     * @return 分页的文件夹列表
     */
    IPage<Folder> getFoldersByAdmin(Page<Folder> page, Long adminId);
    /**
     * 删除文件夹
     * 删除指定ID的文件夹，但仅当该文件夹为空且为当前管理员创建时才允许删除
     *
     * @param folderId 文件夹ID
     * @param adminId 管理员ID
     * @return 删除是否成功
     */
    boolean deleteFolder(Long folderId, Long adminId);
    /**
     * 查询用户有读写/管理权限的文件夹
     * 获取用户拥有读写或管理权限的文件夹列表（权限级别>=2）
     *
     * @param userId 用户ID
     * @return 有权限访问的文件夹列表
     */
    List<Folder> getAuthorizedFolders(Long userId);

    /**
     * 检查并删除某管理员创建的所有文件夹
     * 在删除管理员前，先检查并删除该管理员创建的所有文件夹
     * 预检查是否存在非空文件夹，如存在则返回false阻止删除管理员
     *
     * @param adminId 管理员ID
     * @return 删除是否成功
     */
    boolean checkAndDeleteByCreatorId(Long adminId);
}