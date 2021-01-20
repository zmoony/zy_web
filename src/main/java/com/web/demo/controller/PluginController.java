package com.web.demo.controller;

import com.web.demo.common.R;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author yuez
 * @title: PluginController
 * @projectName zy_web
 * @description: 插件研究的控制层
 * @date 2021/1/7 15:41
 */
@RestController
@RequestMapping("/plugin")
public class PluginController {

    @PostMapping("/webUploader/upload")
    public R upload (MultipartFile file, HttpServletRequest request) throws IOException {
        String filename = file.getOriginalFilename();
        String path = FileSystemView.getFileSystemView() .getHomeDirectory().getAbsolutePath();//当前桌面地址
        path = path + File.separator + filename;
        File destFile = new File(path);
        int copy = FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(destFile));
        if(copy > 0){
            return R.ok();
        }
        return R.error();
    }
}
