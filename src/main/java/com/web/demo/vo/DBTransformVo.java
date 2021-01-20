
package com.web.demo.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@SuppressWarnings("unused")
public class DBTransformVo {

    private String author = "aurora";
    private String ddl;
    private String nameRex;
    private String result;
    private String type;

    private String ip;
    private String username;
    private String password;
    private String tableName;
    private String sql;


}
