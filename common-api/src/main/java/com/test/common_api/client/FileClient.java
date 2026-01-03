/**
 * 文件服务客户端接口
 * 提供与其他服务通信的文件和文件夹相关API接口
 */
package com.test.common_api.client;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
    /**
     * 根据文件夹ID获取文件列表（分页）
     *
     * @param page 分页对象
     * @param folderId 文件夹ID
     * @return 分页的文件列表
     */
    @PostMapping("/getByFolderId")
    public Page<File> getByFolderId(@RequestBody Page<File> page, @RequestParam("folderId") Long folderId);

    /**
     * 获取所有文件（分页）
     *
     * @param page 分页对象
     * @return 分页的文件列表
     */
    @PostMapping("/getAll")
    public Page<File> getAll(@RequestBody Page<File> page);

    /**
     * 上传文件
     *
     * @param fileList 文件列表
     * @param userId 上传用户ID
     * @param folderId 目标文件夹ID
     * @param ipAddress 客户端IP地址
     * @return 上传是否成功
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public boolean upload(
            @RequestPart("file") List<MultipartFile> fileList,
            @RequestParam("userId") Long userId,
            @RequestParam("folderId") Long folderId,
            @RequestParam("ipAddress") String ipAddress
    );

    /**
     * 更新文件
     * 替换指定ID的文件内容
     *
     * @param fileId 要更新的文件ID
     * @param file 新的文件内容
     * @param userId 操作用户ID
     * @param ipAddress 客户端IP地址
     * @return 更新是否成功
     */
    // 新增：文件更新接口
    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public boolean update(
            @RequestParam("fileId") Long fileId,
            @RequestPart("file") MultipartFile file,
            @RequestParam("userId") Long userId,
            @RequestParam("ipAddress") String ipAddress
    );

    /**
     * 根据用户ID获取文件列表
     *
     * @param userId 用户ID
     * @return 文件列表
     */
    @GetMapping("/getByUserId/{userId}")
    public List<File> getByUserId(@PathVariable("userId") Long userId);

    /**
     * 获取用户可访问的文件列表
     *
     * @param userId 用户ID
     * @return 可访问的文件列表
     */
    @GetMapping("/getAccessibleFiles/{userId}")
    public List<File> getAccessibleFiles(@PathVariable("userId") Long userId);

    /**
     * 根据ID获取文件信息
     *
     * @param id 文件ID
     * @return 文件对象
     */
    @GetMapping("/getById/{id}")
    public File getById(@PathVariable("id") Long id);

    /**
     * 删除文件
     *
     * @param id 文件ID
     * @param userId 用户ID
     * @return 删除是否成功
     */
    @GetMapping("/delete/{id}/{userId}")
    public boolean delete(@PathVariable("id") Long id, @PathVariable("userId") Long userId);

    /**
     * 根据用户ID删除所有文件
     *
     * @param userId 用户ID
     * @return 删除是否成功
     */
    @GetMapping("/deleteByUserId/{userId}")
    public boolean deleteByUserId(@PathVariable("userId") Long userId);

    /**
     * 管理员获取文件夹列表（分页）
     *
     * @param page 分页对象
     * @param userId 管理员ID
     * @return 分页的文件夹列表
     */
    @PostMapping("/admin/folders/getFoldersByAdmin")
    public Page<Folder> getFoldersByAdmin(@RequestBody Page<Folder> page, @RequestParam("adminId") Long userId);

    /**
     * 创建文件夹
     *
     * @param folderName 文件夹名称
     * @param adminId 管理员ID
     * @return 创建是否成功
     */
    @GetMapping("/admin/folders/createFolder/{folderName}/{adminId}")
    public boolean createFolder(@PathVariable("folderName") String folderName, @PathVariable("adminId") Long adminId);

    /**
     * 根据ID获取文件夹信息
     *
     * @param folderId 文件夹ID
     * @return 文件夹对象
     */
    @GetMapping("/admin/folders/getById/{folderId}")
    public Folder getFolderById(@PathVariable("folderId") Long folderId);

    /**
     * 删除文件夹
     *
     * @param folderId 文件夹ID
     * @param adminId 管理员ID
     * @return 删除是否成功
     */
    @GetMapping("/admin/folders/deleteFolder/{folderId}/{adminId}")
    public boolean deleteFolder(@PathVariable("folderId") Long folderId, @PathVariable("adminId") Long adminId);

    /**
     * 获取所有文件夹列表
     *
     * @return 文件夹列表
     */
    @GetMapping("/admin/folders/folder/list")
    public List<Folder> list();

    /**
     * 获取用户有权限访问的文件夹列表
     *
     * @param userId 用户ID
     * @return 有权限访问的文件夹列表
     */
    @GetMapping("/admin/folders/getAuthorizedFolders/{userId}")
    public List<Folder> getAuthorizedFolders(@PathVariable("userId") Long userId);

    /**
     * 根据查询条件获取文件夹列表
     *
     * @param qw 查询条件
     * @return 符合条件的文件夹列表
     */
    @PostMapping("/admin/folders/folder/list/wrapper")
    public List<Folder> list(@RequestBody QueryWrapper<Folder> qw);

    /**
     * 获取文件夹的权限列表
     *
     * @param folderId 文件夹ID
     * @return 文件夹权限列表
     */
    @GetMapping("/admin/folders/getPermissionsByFolder/{folderId}")
    public List<FolderPermission> getPermissionsByFolder(@PathVariable("folderId") Long folderId);

    /**
     * 为用户授予文件夹权限
     *
     * @param folderId 文件夹ID
     * @param userId 用户ID
     * @param permission 权限级别（1-只读，2-读写，3-管理）
     * @return 授权是否成功
     */
    @GetMapping("/admin/folders/grantPermission/{folderId}/{userId}/{permission}")
    public boolean grantPermission(@PathVariable("folderId") Long folderId, @PathVariable("userId") Long userId, @PathVariable("permission") Integer permission);

    /**
     * 删除用户对文件夹的权限
     *
     * @param folderId 文件夹ID
     * @param userId 用户ID
     * @return 删除权限是否成功
     */
    @GetMapping("/admin/folders/deletePermission/{folderId}/{userId}")
    public boolean deletePermission(@PathVariable("folderId") Long folderId, @PathVariable("userId") Long userId);

    /**
     * 检查用户对文件夹的权限
     *
     * @param userId 用户ID
     * @param folderId 文件夹ID
     * @return 权限级别（1-只读，2-读写，3-管理），无权限则返回null
     */
    @GetMapping("/admin/folders/checkPermission/{userId}/{folderId}")
    public Integer checkPermission(@PathVariable("userId") Long userId, @PathVariable("folderId") Long folderId);

    /**
     * 根据用户ID删除文件夹
     *
     * @param userId 用户ID
     * @return 删除是否成功
     */
    @GetMapping("/admin/folders/deleteByUserId/{userId}")
    public boolean deleteFolderByUserId(@PathVariable("userId") Long userId);

    /**
     * 检查并删除某管理员创建的所有文件夹（修复删除管理员遗留数据问题）
     *
     * @param adminId 管理员ID
     * @return 删除是否成功
     */
    @GetMapping("/admin/folders/checkAndDeleteByCreator/{adminId}")
    public boolean checkAndDeleteByCreator(@PathVariable("adminId") Long adminId);
}