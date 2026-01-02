package com.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.common_api.client.OperationLogClient;
import com.test.entity.File;
import com.test.mapper.FileMapper;
import com.test.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 文件服务实现类
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {
    
    @Autowired
    private FileMapper fileMapper;
    
    @Autowired
    private OperationLogClient operationLogClient;
    
    @Value("${file.upload-path}")
    private String uploadPath;
    
    @Override
    public boolean upload(MultipartFile file, Long userId, Long folderId, String ipAddress) {
        try {
            // 确保上传目录存在
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            
            // 保存文件
            Path targetPath = uploadDir.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetPath);
            
            // 保存文件信息到数据库
            File fileEntity = new File();
            fileEntity.setFileName(originalFilename);
            fileEntity.setFilePath(targetPath.toString());
            fileEntity.setFileSize(file.getSize());
            fileEntity.setFileType(file.getContentType());
            fileEntity.setUserId(userId);
            fileEntity.setCreateTime(new Date());
            fileEntity.setUpdateTime(new Date());

            // 新增：关联文件夹ID
            fileEntity.setFolderId(folderId);
            boolean saveResult = save(fileEntity);

            // 记录操作日志
            if (saveResult) {
                operationLogClient.recordLog(userId, fileEntity.getId(), "upload", ipAddress);
            }
            
            return saveResult;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional // 保证事务一致性
    public boolean delete(Long id, Long userId) {
        File file = getById(id);
        if (file == null) {
            return false;
        }

        // 1. 先删除该文件对应的所有操作日志
        operationLogClient.deleteByFileId(id);

        // 2. 再删除文件记录
        boolean deleteResult = removeById(id);

        // 3. 删除本地文件
        if (deleteResult) {
            Path filePath = Paths.get(file.getFilePath());
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                log.error("删除本地文件失败", e);
                // 可根据业务需求决定是否回滚事务
            }
        }

        return deleteResult;
    }
    
    @Override
    public File getById(Long id) {
        return fileMapper.selectById(id);
    }
    
    @Override
    public List<File> getByUserId(Long userId) {
        return fileMapper.selectByUserId(userId);
    }
    
    @Override
    @Transactional
    public List<File> getAll() {
        return fileMapper.selectAll();
    }

    @Override
    public List<File> getByFolderId(Long folderId) {
        // 使用MyBatis，根据folderId查询文件
        return fileMapper.selectByFolderId(folderId);
    }

    @Override
    @Transactional
    public IPage<File> getAll(Page<File> page) {
        return fileMapper.selectPage(page, null);
    }

    @Override
    public IPage<File> getByFolderId(Page<File> page, Long folderId) {
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("folder_id", folderId);
        return fileMapper.selectPage(page, queryWrapper);
    }

    @Override
    public boolean deleteByUserId(Long userId) {
        // 实现逻辑：删除该用户相关的所有文件
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId); // 假设文件表中有user_id字段关联用户
        baseMapper.delete(queryWrapper);
        return true;
    }
}