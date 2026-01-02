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
        log.debug("getByFolderId:接收到的参数：page:{},folderId:{}",page,folderId);
        IPage<File> filePage = fileService.getByFolderId(page, folderId);
        log.debug("fileService.getByFolderId:{}",filePage);
        return (Page<File>) filePage;
    }
    @PostMapping("/getAll")
    public Page<File> getAll(@RequestBody Page<File> page){
        log.debug("getAll:接收到的参数：page:{}",page);
        IPage<File> filePage = fileService.getAll(page);
        log.debug("fileService.getAll:{}",filePage);
        return (Page<File>) filePage;
    }
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public boolean upload(
            @RequestPart("fileList") List<MultipartFile> fileList,
            @RequestParam("userId") Long userId,
            @RequestParam("folderId") Long folderId,
            @RequestParam("ipAddress") String ipAddress
    ){
        log.debug("upload:接收到的参数：userId:{}\nfolderId:{}\nipAddress:{}",userId,folderId,ipAddress);
        MultipartFile file = fileList.get(0);
        boolean upload = fileService.upload(file, userId, folderId, ipAddress);
        log.debug("fileService.upload:{}",upload);
        return upload;
    }
    @GetMapping("/getByUserId/{userId}")
    public List<File> getByUserId(@PathVariable("userId") Long userId){
        log.debug("getByUserId:接收到的参数：userId:{}",userId);
        List<File> files = fileService.getByUserId(userId);
        log.debug("fileService.getByUserId:{}",files);
        return files;
    }
    @GetMapping("/getById/{id}")
    public File getById(@PathVariable("id") Long id){
        log.debug("getById:接收到的参数：id:{}",id);
        File file = fileService.getById(id);
        log.debug("fileService.getById:{}",file);
        return file;
    }
    @GetMapping("/delete/{id}/{userId}")
    public boolean delete(@PathVariable("id") Long id,@PathVariable("userId") Long userId){
        log.debug("delete:接收到的参数：id:{},userId:{}",id,userId);
        boolean delete = fileService.delete(id, userId);
        log.debug("fileService.delete:{}",delete);
        return delete;
    }
    @GetMapping("/deleteByUserId/{userId}")
    public boolean deleteByUserId(@PathVariable("userId") Long userId){
        log.debug("deleteByUserId:接收到的参数：userId:{}",userId);
        boolean b = fileService.deleteByUserId(userId);
        log.debug("fileService.deleteByUserId:{}",b);
        return b;
    }
}