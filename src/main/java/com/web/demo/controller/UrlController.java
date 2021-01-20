package com.web.demo.controller;

import com.web.demo.init.ServiceBigdataProperties;
import com.web.demo.util.DataCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author yuez
 * @title: UrlController
 * @projectName zy_web
 * @description: 页面跳转
 * @date 2020/8/28 11:08
 */
@Controller
public class UrlController {
    @Autowired
    private ServiceBigdataProperties serviceBigdataProperties;

    /******************************初始化*****************************************/
    @ModelAttribute
    public void common(Model model) {
        model.addAttribute("bigdataUrl", serviceBigdataProperties.getUrl());
        model.addAttribute("urlCache", DataCache.urlCache);
    }

    @GetMapping(value = {"/", "/login"})
    public String login() {
        return "login";
    }

    @GetMapping(value = {"/util"})
    public String index() {
        return "index";
    }

    @GetMapping(value = " /util/edit")
    public String util_edit() {
        return "util-edit";
    }

    @GetMapping(value = "/header")
    public String header() {
        return "header";
    }

    @GetMapping(value = "/footer")
    public String footer() {
        return "footer";
    }

    @GetMapping(value = "/plugin/index")
    public String pluginIndex(){
        return "plugin-index";
    }

    /******************************数据库*****************************************/
    @GetMapping(value = "/db/export")
    public String dbExport() {
        return "db/db-export";
    }

    @GetMapping(value = "/db/transform")
    public String dbTransform() {
        return "db/db-transform";
    }

    /******************************通用工具*****************************************/
    @GetMapping(value = "/maven/local")
    public String mavenLocal() {
        return "day/maven-local";
    }

    @GetMapping(value = "/ps/util")
    public String psUtil() {
        return "day/ps-util";
    }

    @GetMapping(value = "/common/util")
    public String commonUtil() {
        return "day/common-util";
    }

    @GetMapping(value = "/pic/util")
    public String picUtil() {
        return "day/pic-util";
    }
    /******************************插件工具*****************************************/
    @GetMapping(value = "/plugin/jstree")
    public String pluginJstree() {
        return "plugin/jstree-util";
    }
    @GetMapping(value = "/plugin/webUploader")
    public String pluginWebUploader() {
        return "plugin/webUploader-util";
    }
}
