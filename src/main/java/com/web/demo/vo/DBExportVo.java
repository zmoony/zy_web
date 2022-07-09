package com.web.demo.vo;

import lombok.Data;

/**
 * @author yuez
 * @title: DBExportVo
 * @projectName zy_web
 * @description: 表结构导出
 * @date 2020/8/28 15:58
 */
@Data
public class DBExportVo {
    private String desc;
    private String designatedTableName;
    private String designatedTablePrefix;
    private String designatedTableSuffix;
    private String dest;
    private String fileType;
    private String ignoreTableName;
    private String ignoreTablePrefix;
    private String ignoreTableSuffix;
    private String ip;
    private String password;
    private String type;
    private String username;
    private String version;
    private String serviceId;
    private String port;
}
