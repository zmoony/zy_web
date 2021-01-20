package com.web.demo.util;

import org.apache.tomcat.util.collections.CaseInsensitiveKeyMap;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yuez
 * @title: DBTool
 * @projectName zy_web
 * @description: 数据库字段转换
 * @date 2020/9/1 10:48
 */
public class DBTool {
    //字段对应
    public static String columnMapper(int columnType) {
        Map<Integer,String> mapper = new HashMap(){{
            put(Types.CHAR,"String");
            put(Types.VARCHAR,"String");
            put(Types.NUMERIC,"Integer");
            put(Types.INTEGER,"Integer");
            put(Types.TIMESTAMP,"Date");
            put(Types.DATE,"Date");
            put(Types.DOUBLE,"Double");
            put(Types.FLOAT,"Double");
            put(Types.CLOB,"Clob");
            put(Types.BLOB,"Blob");
        }};
        return mapper.get(columnType);
    }
    //建表语句对应
    public static String sqlMapper(String columnType) {
       Map<String, String> mapper = new CaseInsensitiveKeyMap<String>(){{
            put("int", "Integer");
            put("bigint", "Integer");
            put("NUMBER", "Integer");
            put("varchar2", "String");
            put("char", "String");
            put("varchar", "String");
            put("double", "Double");
            put("float", "Float");
            put("bit", "Boolean");
            put("datetime", "Date");
            put("date", "Date");
        }};
        return mapper.get(columnType);
    }


}
