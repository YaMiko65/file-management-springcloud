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
 */
@RestController
@Slf4j
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/getByFolderId")
    public Page<File> getByFolderId(@RequestBody Page<File> page,@RequestParam("folderId") Long folderId){
        IPage<File> filePage = fileService.getByFolderId(page, folderId);
        return (Page<File>) filePage;
    }

    @PostMapping("/getAll")
    public Page<File> getAll(@RequestBody Page<File> page){
        IPage<File> filePage = fileService.getAll(page);
        return (Page<File>) filePage;
    }

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

    @GetMapping("/getByUserId/{userId}")
    public List<File> getByUserId(@PathVariable("userId") Long userId){
        return fileService.getByUserId(userId);
    }

    @GetMapping("/getAccessibleFiles/{userId}")
    public List<File> getAccessibleFiles(@PathVariable("userId") Long userId){
        return fileService.getAccessibleFiles(userId);
    }

    @GetMapping("/getById/{id}")
    public File getById(@PathVariable("id") Long id){
        return fileService.getById(id);
    }

    @GetMapping("/delete/{id}/{userId}")
    public boolean delete(@PathVariable("id") Long id,@PathVariable("userId") Long userId){
        return fileService.delete(id, userId);
    }

    @GetMapping("/deleteByUserId/{userId}")
    public boolean deleteByUserId(@PathVariable("userId") Long userId){
        return fileService.deleteByUserId(userId);
    }
}