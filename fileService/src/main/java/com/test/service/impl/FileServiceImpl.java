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
 * 实现文件上传、下载、删除等核心功能
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

        /**
     * 上传文件
     * 将用户上传的文件保存到指定文件夹中，并记录操作日志
     *
     * @param file 上传的文件
     * @param userId 上传用户ID
     * @param folderId 目标文件夹ID
     * @param ipAddress 客户端IP地址
     * @return 上传是否成功
     */
    @Override
    public boolean upload(MultipartFile file, Long userId, Long folderId, String ipAddress) {
        User user = userClient.getById(userId);
        if (user == null) {
            log.error("上传失败：用户不存在 userId={}", userId);
            return false;
        }

        Folder folder = folderService.getById(folderId);
        if (folder == null) {
            log.error("上传失败：文件夹不存在 folderId={}", folderId);
            return false;
        }

        if (user.getRole() != 1) {
            Integer permission = folderPermissionService.checkPermission(userId, folderId);
            if (permission == null || permission < 2) {
                log.warn("安全警告：用户 {} 尝试向文件夹 {} 上传文件但无写权限", userId, folderId);
                return false;
            }
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
     * 替换指定ID的文件内容，同时删除旧的物理文件并记录操作日志
     *
     * @param fileId 要更新的文件ID
     * @param newFile 新的文件内容
     * @param userId 操作用户ID
     * @param ipAddress 客户端IP地址
     * @return 更新是否成功
     */
    @Override
    @Transactional
    public boolean update(Long fileId, MultipartFile newFile, Long userId, String ipAddress) {
        // 1. 获取原文件信息
        File fileEntity = getById(fileId);
        if (fileEntity == null) {
            log.error("更新失败：文件不存在 fileId={}", fileId);
            return false;
        }

        // 2. 权限校验
        User user = userClient.getById(userId);
        if (user == null) return false;

        boolean isOwner = fileEntity.getUserId().equals(userId);
        boolean isAdmin = (user.getRole() != null && user.getRole() == 1);
        Integer permission = folderPermissionService.checkPermission(userId, fileEntity.getFolderId());
        boolean hasFolderWritePermission = (permission != null && permission >= 2);

        // 如果既不是所有者，也不是管理员，也没有文件夹写权限，则拒绝
        if (!isOwner && !isAdmin && !hasFolderWritePermission) {
            log.warn("用户 {} 尝试更新文件 {} 但无权限", userId, fileId);
            return false;
        }

        try {
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

            // 4. 保存新文件
            Files.copy(newFile.getInputStream(), targetPath);

            // 5. 删除旧物理文件
            Path oldFilePath = Paths.get(fileEntity.getFilePath());
            try {
                Files.deleteIfExists(oldFilePath);
            } catch (IOException e) {
                log.warn("删除旧文件失败，但不影响更新流程: {}", oldFilePath);
            }

            // 6. 更新数据库记录
            fileEntity.setFileName(originalFilename); // 更新为新文件名
            fileEntity.setFilePath(targetPath.toString());
            fileEntity.setFileSize(newFile.getSize());
            fileEntity.setFileType(newFile.getContentType());
            fileEntity.setUpdateTime(new Date()); // 更新时间
            // userId 和 folderId 保持不变

            boolean updateResult = updateById(fileEntity);

            // 7. 记录日志
            if (updateResult) {
                operationLogClient.recordLog(userId, fileEntity.getId(), "update", ipAddress);
            }

            return updateResult;

        } catch (IOException e) {
            log.error("文件更新异常", e);
            return false;
        }
    }

        /**
     * 删除文件
     * 删除指定ID的文件，包括数据库记录和物理文件，并记录操作日志
     *
     * @param id 文件ID
     * @param userId 操作用户ID
     * @return 删除是否成功
     */
    @Override
    @Transactional
    public boolean delete(Long id, Long userId) {
        File file = getById(id);
        if (file == null) {
            return false;
        }

        User user = userClient.getById(userId);
        if (user == null) {
            return false;
        }

        boolean isOwner = file.getUserId().equals(userId);
        boolean isAdmin = (user.getRole() != null && user.getRole() == 1);
        Integer permission = folderPermissionService.checkPermission(userId, file.getFolderId());
        boolean hasFolderPermission = (permission != null && permission >= 2);

        if (!isOwner && !isAdmin && !hasFolderPermission) {
            log.warn("用户 {} 尝试删除文件 {} 但无权限", userId, id);
            return false;
        }

        operationLogClient.deleteByFileId(id);
        boolean deleteResult = removeById(id);

        if (deleteResult) {
            Path filePath = Paths.get(file.getFilePath());
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                log.error("删除本地文件失败", e);
            }
        }

        return deleteResult;
    }

        /**
     * 根据ID获取文件信息
     *
     * @param id 文件ID
     * @return 文件对象
     */
    @Override
    public File getById(Long id) {
        return fileMapper.selectById(id);
    }

        /**
     * 根据用户ID获取文件列表
     *
     * @param userId 用户ID
     * @return 文件列表
     */
    @Override
    public List<File> getByUserId(Long userId) {
        return fileMapper.selectByUserId(userId);
    }

        /**
     * 获取用户可访问的文件列表
     * 复用 selectByUserId (已在Defect 1修复中修改为查询所有可访问文件)
     *
     * @param userId 用户ID
     * @return 用户可访问的文件列表
     */
    @Override
    public List<File> getAccessibleFiles(Long userId) {
        return fileMapper.selectByUserId(userId);
    }

        /**
     * 获取所有文件列表
     *
     * @return 所有文件列表
     */
    @Override
    @Transactional
    public List<File> getAll() {
        return fileMapper.selectAll();
    }

        /**
     * 根据文件夹ID获取文件列表
     *
     * @param folderId 文件夹ID
     * @return 文件夹中的文件列表
     */
    @Override
    public List<File> getByFolderId(Long folderId) {
        return fileMapper.selectByFolderId(folderId);
    }

        /**
     * 获取所有文件（分页）
     *
     * @param page 分页对象
     * @return 分页的文件列表
     */
    @Override
    @Transactional
    public IPage<File> getAll(Page<File> page) {
        return fileMapper.selectPage(page, null);
    }

        /**
     * 根据文件夹ID获取文件（分页）
     *
     * @param page 分页对象
     * @param folderId 文件夹ID
     * @return 分页的文件列表
     */
    @Override
    public IPage<File> getByFolderId(Page<File> page, Long folderId) {
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("folder_id", folderId);
        return fileMapper.selectPage(page, queryWrapper);
    }

        /**
     * 根据用户ID删除所有文件
     * 删除指定用户上传的所有文件
     *
     * @param userId 用户ID
     * @return 删除是否成功
     */
    @Override
    @Transactional
    public boolean deleteByUserId(Long userId) {
        List<File> files = fileMapper.selectByUserId(userId);
        if (files == null || files.isEmpty()) {
            return true;
        }

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