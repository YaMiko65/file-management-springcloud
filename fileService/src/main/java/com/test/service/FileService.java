package com.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.test.entity.File;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件服务接口
 */
public interface FileService extends IService<File> {

    /**
     * 上传文件
     */
    boolean upload(MultipartFile file, Long userId, Long folderId, String ipAddress);

    /**
     * 更新文件（重传）
     */
    boolean update(Long fileId, MultipartFile file, Long userId, String ipAddress);

    /**
     * 删除文件
     */
    boolean delete(Long id, Long userId);

    File getById(Long id);

    /**
     * 根据用户ID获取文件
     */
    List<File> getByUserId(Long userId);

    /**
     * 获取用户有权限访问的所有文件
     */
    List<File> getAccessibleFiles(Long userId);

    List<File> getAll();

    List<File> getByFolderId(Long folderId);

    IPage<File> getAll(Page<File> page);

    IPage<File> getByFolderId(Page<File> page, Long folderId);

    boolean deleteByUserId(Long userId);
}