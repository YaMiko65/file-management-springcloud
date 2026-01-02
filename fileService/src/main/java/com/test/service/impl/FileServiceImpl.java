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
        // 1. 校验用户有效性
        User user = userClient.getById(userId);
        if (user == null) {
            log.error("上传失败：用户不存在 userId={}", userId);
            return false;
        }

        // 2. 校验文件夹是否存在
        Folder folder = folderService.getById(folderId);
        if (folder == null) {
            log.error("上传失败：文件夹不存在 folderId={}", folderId);
            return false;
        }

        // 3. 校验上传权限
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

        // --- 修复开始：扩展权限校验逻辑 ---

        // 1. 判断是否是文件所有者
        boolean isOwner = file.getUserId().equals(userId);

        // 2. 判断是否是管理员
        boolean isAdmin = (user.getRole() != null && user.getRole() == 1);

        // 3. 判断是否拥有文件夹的读写(2)或管理(3)权限
        Integer permission = folderPermissionService.checkPermission(userId, file.getFolderId());
        boolean hasFolderPermission = (permission != null && permission >= 2);

        // 如果既不是所有者，也不是管理员，也没有文件夹权限，则拒绝删除
        if (!isOwner && !isAdmin && !hasFolderPermission) {
            log.warn("用户 {} 尝试删除文件 {} 但无权限（非管理员、非所有者、无文件夹写权限）", userId, id);
            return false;
        }
        // --- 修复结束 ---

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

    @Override
    public File getById(Long id) {
        return fileMapper.selectById(id);
    }

    @Override
    public List<File> getByUserId(Long userId) {
        return fileMapper.selectByUserId(userId);
    }

    /**
     * 实现 getAccessibleFiles 接口
     * 复用 selectByUserId (已在Defect 1修复中修改为查询所有可访问文件)
     */
    @Override
    public List<File> getAccessibleFiles(Long userId) {
        return fileMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public List<File> getAll() {
        return fileMapper.selectAll();
    }

    @Override
    public List<File> getByFolderId(Long folderId) {
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
    @Transactional
    public boolean deleteByUserId(Long userId) {
        List<File> files = fileMapper.selectByUserId(userId);
        if (files == null || files.isEmpty()) {
            return true;
        }

        for (File file : files) {
            // 批量删除时只删除用户自己上传的文件，避免误删有权限访问但他人的文件
            // 注意：selectByUserId 如果返回了共享文件，这里需要过滤
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