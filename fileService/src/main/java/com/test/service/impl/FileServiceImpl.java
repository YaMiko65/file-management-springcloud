package com.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.common_api.client.OperationLogClient;
import com.test.common_api.client.UserClient;
import com.test.common_api.entity.User;
import com.test.entity.File;
import com.test.entity.Folder;
import com.test.mapper.FileMapper;
import com.test.service.FileService;
import com.test.service.FolderPermissionService;
import com.test.service.FolderService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private OperationLogClient operationLogClient;
    @Autowired
    private UserClient userClient;
    @Autowired
    private FolderService folderService;
    @Autowired
    private FolderPermissionService folderPermissionService;

    @Value("${file.upload-path}")
    private String uploadPath;

    @Override
    public boolean upload(MultipartFile file, Long userId, Long folderId, String ipAddress) {
        User user = userClient.getById(userId);
        if (user == null) return false;

        Folder folder = folderService.getById(folderId);
        if (folder == null) return false;

        if (user.getRole() != 1) {
            Integer permission = folderPermissionService.checkPermission(userId, folderId);
            if (permission == null || permission < 2) return false;
        }

        try {
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) originalFilename = "unknown";

            String fileExtension = "";
            if (originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            Path targetPath = uploadDir.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetPath);

            File fileEntity = new File();
            fileEntity.setFileName(originalFilename);
            fileEntity.setFilePath(targetPath.toString());
            fileEntity.setFileSize(file.getSize());
            fileEntity.setFileType(file.getContentType());
            fileEntity.setUserId(userId);
            fileEntity.setCreateTime(new Date());
            fileEntity.setUpdateTime(new Date());
            fileEntity.setFolderId(folderId);

            boolean saveResult = save(fileEntity);
            if (saveResult) {
                operationLogClient.recordLog(userId, fileEntity.getId(), "upload", ipAddress);
            }
            return saveResult;
        } catch (IOException e) {
            log.error("文件上传异常", e);
            return false;
        }
    }

    /**
     * 更新文件
     * [修改] 移除 @Transactional 防止 I/O 阻塞数据库事务，增加全局异常捕获
     */
    @Override
    // @Transactional // [修改] 移除事务注解，文件I/O操作耗时较长，不要放在数据库事务中
    public boolean update(Long fileId, MultipartFile newFile, Long userId, String ipAddress) {
        try {
            // 1. 获取原文件信息
            File fileEntity = getById(fileId);
            if (fileEntity == null) {
                log.error("更新失败：文件不存在 fileId={}", fileId);
                return false;
            }

            // 2. 权限校验
            User user = userClient.getById(userId);
            if (user == null) {
                log.error("更新失败：用户不存在 userId={}", userId);
                return false;
            }

            boolean isOwner = fileEntity.getUserId().equals(userId);
            boolean isAdmin = (user.getRole() != null && user.getRole() == 1);
            Integer permission = folderPermissionService.checkPermission(userId, fileEntity.getFolderId());
            boolean hasFolderWritePermission = (permission != null && permission >= 2);

            if (!isOwner && !isAdmin && !hasFolderWritePermission) {
                log.warn("权限不足：用户 {} 尝试更新文件 {}", userId, fileId);
                return false;
            }

            // 3. 准备新文件存储
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String originalFilename = newFile.getOriginalFilename();
            if (originalFilename == null) originalFilename = "unknown";

            String fileExtension = "";
            if (originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            Path targetPath = uploadDir.resolve(uniqueFilename);

            // 4. 保存新文件 (耗时IO)
            Files.copy(newFile.getInputStream(), targetPath);

            // 5. 删除旧物理文件 (尽力而为)
            if (fileEntity.getFilePath() != null) {
                try {
                    Path oldFilePath = Paths.get(fileEntity.getFilePath());
                    Files.deleteIfExists(oldFilePath);
                } catch (Exception e) {
                    log.warn("旧文件删除失败（可能是被占用），不影响更新流程: {}", e.getMessage());
                }
            }

            // 6. 更新数据库记录
            fileEntity.setFileName(originalFilename);
            fileEntity.setFilePath(targetPath.toString());
            fileEntity.setFileSize(newFile.getSize());
            fileEntity.setFileType(newFile.getContentType());
            fileEntity.setUpdateTime(new Date());

            boolean updateResult = updateById(fileEntity);

            // 7. 记录日志
            if (updateResult) {
                try {
                    operationLogClient.recordLog(userId, fileEntity.getId(), "update", ipAddress);
                } catch (Exception e) {
                    log.warn("日志记录失败", e);
                }
            }

            return updateResult;

        } catch (Exception e) {
            // [修改] 捕获所有异常，返回false而不是抛出500
            log.error("文件更新过程中发生异常: ", e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean delete(Long id, Long userId) {
        File file = getById(id);
        if (file == null) return false;

        User user = userClient.getById(userId);
        if (user == null) return false;

        boolean isOwner = file.getUserId().equals(userId);
        boolean isAdmin = (user.getRole() != null && user.getRole() == 1);
        Integer permission = folderPermissionService.checkPermission(userId, file.getFolderId());
        boolean hasFolderPermission = (permission != null && permission >= 2);

        if (!isOwner && !isAdmin && !hasFolderPermission) return false;

        operationLogClient.deleteByFileId(id);
        boolean deleteResult = removeById(id);

        if (deleteResult) {
            try {
                Path filePath = Paths.get(file.getFilePath());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                log.error("删除本地文件失败", e);
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
    public List<File> getAccessibleFiles(Long userId) {
        return fileMapper.selectByUserId(userId);
    }

    @Override
    public List<File> getAll() {
        return fileMapper.selectAll();
    }

    @Override
    public List<File> getByFolderId(Long folderId) {
        return fileMapper.selectByFolderId(folderId);
    }

    @Override
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
    @Transactional
    public boolean deleteByUserId(Long userId) {
        List<File> files = fileMapper.selectByUserId(userId);
        if (files == null || files.isEmpty()) return true;

        for (File file : files) {
            if (file.getUserId().equals(userId)) {
                operationLogClient.deleteByFileId(file.getId());
                removeById(file.getId());
                try {
                    Path filePath = Paths.get(file.getFilePath());
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    log.error("删除本地文件失败: " + file.getFilePath(), e);
                }
            }
        }
        return true;
    }
}