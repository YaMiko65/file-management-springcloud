package com.test;

import com.test.service.FolderPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Test {
    @Autowired
    private FolderPermissionService folderPermissionService;
    @org.junit.jupiter.api.Test
    public void test(){
        Integer i = folderPermissionService.checkPermission(2L, 1L);
        System.out.println(i);
    }
}
