package com.test.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.test.entity.Folder;
import com.test.entity.FolderPermission;
import com.test.service.FolderPermissionService;
import com.test.service.FolderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员文件夹管理
 */

@RestController
@RequestMapping("/admin/folders")
@Slf4j
public class AdminFolderController {

    @Autowired
    private FolderService folderService;
    @Autowired
    private FolderPermissionService permissionService;


    @PostMapping("/getFoldersByAdmin")
    public Page<Folder> getFoldersByAdmin(@RequestBody Page<Folder> page,@RequestParam("adminId") Long adminId){
        log.debug("getFoldersByAdmin:接收到的参数：page:{},adminId:{}",page,adminId);
        IPage<Folder> folderPage = folderService.getFoldersByAdmin(page, adminId);
        log.debug("folderService.getFoldersByAdmin:{}",folderPage);
        return (Page<Folder>) folderPage;
    }
    @GetMapping("/createFolder/{folderName}/{adminId}")
    public boolean createFolder(@PathVariable("folderName") String folderName,@PathVariable("adminId") Long adminId){
        log.debug("createFolder:接收到的参数：folderName:{},adminId:{}",folderName,adminId);
        boolean folder = folderService.createFolder(folderName, adminId);
        log.debug("folderService.createFolder:{}",folder);
        return folder;
    }
    @GetMapping("/getById/{folderId}")
    public Folder getById(@PathVariable("folderId") Long folderId){
        log.debug("getById:接收到的参数：folderId:{}",folderId);
        Folder folder = folderService.getById(folderId);
        log.debug("folderService.getById:{}",folder);
        return folder;
    }
    @GetMapping("/deleteFolder/{folderId}/{adminId}")
    public boolean deleteFolder(@PathVariable("folderId") Long folderId,@PathVariable("adminId") Long adminId){
        log.debug("deleteFolder:接收到的参数：folderId:{},adminId:{}",folderId,adminId);
        boolean b = folderService.deleteFolder(folderId, adminId);
        log.debug("folderService.deleteFolder:{}",b);
        return b;
    }
    @GetMapping("/folder/list")
    @Transactional
    public List<Folder> list(){
        List<Folder> list = folderService.list();
        log.debug("folderService.list:{}",list);
        return list;
    }
    @PostMapping("/folder/list/wrapper")
    @Transactional
    public List<Folder> list(@RequestBody QueryWrapper<Folder> qw){
        List<Folder> list = folderService.list(qw);
        log.debug("folderService.list(qw):{}",list);
        return list;
    }
    @GetMapping("/getAuthorizedFolders/{userId}")
    public List<Folder> getAuthorizedFolders(@PathVariable("userId") Long userId){
        log.debug("getAuthorizedFolders:接收到的参数：userId:{}",userId);
        List<Folder> authorizedFolders = folderService.getAuthorizedFolders(userId);
        log.debug("folderService.getAuthorizedFolders:{}",authorizedFolders);
        return authorizedFolders;
    }
    @GetMapping("/getPermissionsByFolder/{folderId}")
    public List<FolderPermission> getPermissionsByFolder(@PathVariable("folderId") Long folderId){
        log.debug("getPermissionsByFolder:接收到的参数：folderId:{}",folderId);
        List<FolderPermission> permissions = permissionService.getPermissionsByFolder(folderId);
        log.debug("permissionService.getPermissionsByFolder:{}",permissions);
        return permissions;
    }
    @GetMapping("/grantPermission/{folderId}/{userId}/{permission}")
    public boolean grantPermission(@PathVariable("folderId") Long folderId,@PathVariable("userId") Long userId,@PathVariable("permission") Integer permission){
        log.debug("grantPermission:接收到的参数：folderId:{},userId:{},permission:{}",folderId,userId,permission);
        boolean b = permissionService.grantPermission(folderId, userId, permission);
        log.debug("permissionService.grantPermission:{}",b);
        return b;
    }
    @GetMapping("/deletePermission/{folderId}/{userId}")
    public boolean deletePermission(@PathVariable("folderId") Long folderId,@PathVariable("userId") Long userId){
        log.debug("deletePermission:接收到的参数：folderId:{},userId:{}",folderId,userId);
        boolean b = permissionService.deletePermission(folderId, userId);
        log.debug("permissionService.deletePermission:{}",b);
        return b;
    }
    @GetMapping("/checkPermission/{userId}/{folderId}")
    public Integer checkPermission(@PathVariable("userId") Long userId,@PathVariable("folderId") Long folderId){
        log.debug("checkPermission:接收到的参数：folderId:{},userId:{}",folderId,userId);
        Integer i = permissionService.checkPermission(userId, folderId);
        log.debug("permissionService.checkPermission:{}",i);
        return i;
    }
    @GetMapping("/deleteByUserId/{userId}")
    public boolean deleteByUserId(@PathVariable("userId") Long userId){
        log.debug("deleteByUserId:接收到的参数：userId:{}",userId);
        boolean b = permissionService.deleteByUserId(userId);
        log.debug("permissionService.deleteByUserId:{}",b);
        return b;
    }

    // 新增：端点
    @GetMapping("/checkAndDeleteByCreator/{adminId}")
    public boolean checkAndDeleteByCreator(@PathVariable("adminId") Long adminId){
        log.debug("checkAndDeleteByCreator:接收到的参数：adminId:{}", adminId);
        boolean b = folderService.checkAndDeleteByCreatorId(adminId);
        log.debug("folderService.checkAndDeleteByCreatorId:{}", b);
        return b;
    }
}