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
     * @param file 上传的文件
     * @param userId 用户ID
     * @param ipAddress IP地址
     * @return 是否上传成功
     */
    // 添加folderId参数
    boolean upload(MultipartFile file, Long userId, Long folderId, String ipAddress);


    /**
     * 删除文件
     * @param id 文件ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean delete(Long id, Long userId);
    
    /**
     * 根据ID获取文件
     * @param id 文件ID
     * @return 文件信息
     */
    File getById(Long id);
    
    /**
     * 根据用户ID获取文件列表
     * @param userId 用户ID
     * @return 文件列表
     */
    List<File> getByUserId(Long userId);
    
    /**
     * 获取所有文件
     * @return 文件列表
     */
    List<File> getAll();

    /**
     * 根据文件夹ID查询文件
     * @param folderId 文件夹ID
     * @return 文件列表
     */
    List<File> getByFolderId(Long folderId);

    // 添加分页查询方法
    IPage<File> getAll(Page<File> page);
    IPage<File> getByFolderId(Page<File> page, Long folderId);

    boolean deleteByUserId(Long userId);
}