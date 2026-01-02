package com.test.common_api.client;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.test.common_api.config.FeignConfiguration;
import com.test.common_api.entity.File;
import com.test.common_api.entity.Folder;
import com.test.common_api.entity.FolderPermission;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "fileService", configuration = FeignConfiguration.class)
public interface FileClient {
    @PostMapping("/getByFolderId")
    public Page<File> getByFolderId(@RequestBody Page<File> page,@RequestParam("folderId") Long folderId);
    @PostMapping("/getAll")
    public Page<File> getAll(@RequestBody Page<File> page);

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public boolean upload(
            @RequestPart("file") List<MultipartFile> fileList,
            @RequestParam("userId") Long userId,
            @RequestParam("folderId") Long folderId,
            @RequestParam("ipAddress") String ipAddress
    );
    @GetMapping("/getByUserId/{userId}")
    public List<File> getByUserId(@PathVariable("userId") Long userId);

    @GetMapping("/getAccessibleFiles/{userId}")
    public List<File> getAccessibleFiles(@PathVariable("userId") Long userId);

    @GetMapping("/getById/{id}")
    public File getById(@PathVariable("id") Long id);
    @GetMapping("/delete/{id}/{userId}")
    public boolean delete(@PathVariable("id") Long id,@PathVariable("userId") Long userId);
    @GetMapping("/deleteByUserId/{userId}")
    public boolean deleteByUserId(@PathVariable("userId") Long userId);
    @PostMapping("/admin/folders/getFoldersByAdmin")
    public Page<Folder> getFoldersByAdmin(@RequestBody Page<Folder> page,@RequestParam("adminId") Long userId);

    @GetMapping("/admin/folders/createFolder/{folderName}/{adminId}")
    public boolean createFolder(@PathVariable("folderName") String folderName, @PathVariable("adminId") Long adminId);
    @GetMapping("/admin/folders/getById/{folderId}")
    public Folder getFolderById(@PathVariable("folderId") Long folderId);
    @GetMapping("/admin/folders/deleteFolder/{folderId}/{adminId}")
    public boolean deleteFolder(@PathVariable("folderId") Long folderId,@PathVariable("adminId") Long adminId);
    @GetMapping("/admin/folders/folder/list")
    public List<Folder> list();
    @GetMapping("/admin/folders/getAuthorizedFolders/{userId}")
    public List<Folder> getAuthorizedFolders(@PathVariable("userId") Long userId);
    @PostMapping("/admin/folders/folder/list/wrapper")
    public List<Folder> list(@RequestBody QueryWrapper<Folder> qw);
    @GetMapping("/admin/folders/getPermissionsByFolder/{folderId}")
    public List<FolderPermission> getPermissionsByFolder(@PathVariable("folderId") Long folderId);
    @GetMapping("/admin/folders/grantPermission/{folderId}/{userId}/{permission}")
    public boolean grantPermission(@PathVariable("folderId") Long folderId,@PathVariable("userId") Long userId,@PathVariable("permission") Integer permission);
    @GetMapping("/admin/folders/deletePermission/{folderId}/{userId}")
    public boolean deletePermission(@PathVariable("folderId") Long folderId,@PathVariable("userId") Long userId);
    @GetMapping("/admin/folders/checkPermission/{userId}/{folderId}")
    public Integer checkPermission(@PathVariable("userId") Long userId,@PathVariable("folderId") Long folderId);
    @GetMapping("/admin/folders/deleteByUserId/{userId}")
    public boolean deleteFolderByUserId(@PathVariable("userId") Long userId);

    // 新增：检查并删除某管理员创建的所有文件夹（修复删除管理员遗留数据问题）
    @GetMapping("/admin/folders/checkAndDeleteByCreator/{adminId}")
    public boolean checkAndDeleteByCreator(@PathVariable("adminId") Long adminId);
}