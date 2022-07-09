package com.web.demo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yuez
 * @title: Tool
 * @projectName zy_web
 * @description: java下划线与驼峰命名互转
 * @date 2020/8/31 11:44
 */
public class JavaTool {
    private static Pattern linePattern = Pattern.compile("_(\\w)");
    /** 首字符大写 */
    public static String firstToUpper(String key) {
       return key.substring(0, 1).toUpperCase() + key.substring(1);
    }
    /** 下划线转驼峰 */
    public static String lineToHump(String str) {
//        str = str.toLowerCase();
        if(str.indexOf("_") == -1){
            return str;
        }
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /** 驼峰转下划线(简单写法，效率低于{@link #humpToLine2(String)}) */
    public static String humpToLine(String str) {
        return str.replaceAll("[A-Z]", "_$0").toLowerCase();
    }

    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    /** 驼峰转下划线,效率比上面高 */
    public static String humpToLine2(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }


}
