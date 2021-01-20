package com.web.demo.util;

import lombok.Data;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author yuez
 * @title: DataCache
 * @projectName zy_web
 * @description: 系统缓存信息
 * @date 2020/11/2 8:57
 */
@Data
public class DataCache {
    public static Map<String,String> urlCache = new LinkedHashMap<>();

    static {
        urlCache.put("","搜索组件或模块");
        urlCache.put("/db/export","表结构导出");
        urlCache.put("/db/transform","表与JAVABEAN的转换");
        urlCache.put("/common/util","常规工具");
        urlCache.put("/maven/local","MAVEN本地仓库管理");
        urlCache.put("/ps/util","PowerShell工具");
        urlCache.put("/pic/util","图片工具");
        urlCache.put("/encode/util","编码工具");
        urlCache.put("/ascii/dic","ASCII码字典");
//        urlCache.put("/plugin/jstree","[插件]jstree");
    }
}
