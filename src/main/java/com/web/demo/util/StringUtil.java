package com.web.demo.util;

/** 
* @ClassName: StringUtil 
* @Description: 字符串工具类
* @author bfan@wiscom.com 
*/  
public final class StringUtil
{
    public final static String EMPTY ="";
    
    /** 
     * 判断字符串是否为空 
     * @param parameter
     * @return boolean
     */  
    public final static boolean isBlank(String parameter)
    {
        if (null == parameter || parameter.trim().isEmpty() || "null".equals(parameter))
        {
            return true;
        }

        return false;
    }
    
    /** 
     * 判断字符串是否非空
     * @param parameter
     * @return boolean
     */  
    public final static boolean isNotBlank(String parameter)
    {
        return !isBlank(parameter);
    }
    
    /** 
     * 变换字符串 
     * @param src
     * @return String
     */  
    public final static String change(String src)
    {
        String target = EMPTY;
        if(isNotBlank(src))
        {
            target = src.replace('\\', '/');
            int start = src.startsWith("/") ? 1 : 0;
            int end = target.length();
            end = target.endsWith("\\") ? end -1 : end;
            // 去除最后的字符
            target = target.substring(start, end);
        }
        return target;
    }
     
}
