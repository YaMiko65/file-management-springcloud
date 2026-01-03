package com.test.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.test.entity.File;
import com.test.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件控制器
 * 提供文件上传、下载、删除等REST API接口
 */
@RestController
@Slf4j
public class FileController {

    @Autowired
    private FileService fileService;

        /**
     * 根据文件夹ID获取文件列表（分页）
     *
     * @param page 分页对象
     * @param folderId 文件夹ID
     * @return 分页的文件列表
     */
    @PostMapping("/getByFolderId")
    public Page<File> getByFolderId(@RequestBody Page<File> page, @RequestParam("folderId") Long folderId){
        IPage<File> filePage = fileService.getByFolderId(page, folderId);
        return (Page<File>) filePage;
    }

        /**
     * 获取所有文件（分页）
     *
     * @param page 分页对象
     * @return 分页的文件列表
     */
    @PostMapping("/getAll")
    public Page<File> getAll(@RequestBody Page<File> page){
        IPage<File> filePage = fileService.getAll(page);
        return (Page<File>) filePage;
    }

        /**
     * 上传文件
     * 接收用户上传的文件并保存到指定文件夹
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
    ){
        if (fileList == null || fileList.isEmpty()) {
            return false;
        }
        MultipartFile file = fileList.get(0);
        return fileService.upload(file, userId, folderId, ipAddress);
    }

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
    ){
        return fileService.update(fileId, file, userId, ipAddress);
    }

        /**
     * 根据用户ID获取文件列表
     *
     * @param userId 用户ID
     * @return 文件列表
     */
    @GetMapping("/getByUserId/{userId}")
    public List<File> getByUserId(@PathVariable("userId") Long userId){
        return fileService.getByUserId(userId);
    }

        /**
     * 获取用户可访问的文件列表
     *
     * @param userId 用户ID
     * @return 用户可访问的文件列表
     */
    @GetMapping("/getAccessibleFiles/{userId}")
    public List<File> getAccessibleFiles(@PathVariable("userId") Long userId){
        return fileService.getAccessibleFiles(userId);
    }

        /**
     * 根据ID获取文件信息
     *
     * @param id 文件ID
     * @return 文件对象
     */
    @GetMapping("/getById/{id}")
    public File getById(@PathVariable("id") Long id){
        return fileService.getById(id);
    }

        /**
     * 删除文件
     *
     * @param id 文件ID
     * @param userId 操作用户ID
     * @return 删除是否成功
     */
    @GetMapping("/delete/{id}/{userId}")
    public boolean delete(@PathVariable("id") Long id, @PathVariable("userId") Long userId){
        return fileService.delete(id, userId);
    }

        /**
     * 根据用户ID删除所有文件
     *
     * @param userId 用户ID
     * @return 删除是否成功
     */
    @GetMapping("/deleteByUserId/{userId}")
    public boolean deleteByUserId(@PathVariable("userId") Long userId){
        return fileService.deleteByUserId(userId);
    }
}