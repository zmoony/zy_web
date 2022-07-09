package com.web.demo.common;

import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一结果类
 * 允许链式编程
 * Created by zy on 2020/4/20.
 */
@Data
@Accessors(chain = true)
public class R {
    private Boolean success;
    private Integer code;
    private String message;
    private Map<String, Object> data = new HashMap<>();
    //构造方法私有化
    private R(){}
    //通用的返回成功
    public static R ok(){
        return new R().setSuccess(ResultCodeEnum.SUCCESS.isSuccess())
                .setCode(ResultCodeEnum.SUCCESS.getCode())
                .setMessage(ResultCodeEnum.SUCCESS.getMessage());
    }
    //通用返回失败，未知错误
    public static R error(){
        return new R().setSuccess(ResultCodeEnum.UNKNOWN_ERROR.isSuccess())
                .setCode(ResultCodeEnum.UNKNOWN_ERROR.getCode())
                .setMessage(ResultCodeEnum.UNKNOWN_ERROR.getMessage());
    }
    //根据结果枚举自定义返回
    public static R setResult(ResultCodeEnum codeEnum){
        return new R().setSuccess(codeEnum.isSuccess())
                .setCode(codeEnum.getCode())
                .setMessage(codeEnum.getMessage());
    }

    // 自定义返回数据
    public R data(Map<String,Object> map) {
        this.setData(map);
        return this;
    }

    // 通用设置data
    public R data(String key,Object value) {
        this.data.put(key, value);
        return this;
    }

    // 自定义状态信息
    public R message(String message) {
        this.setMessage(message);
        return this;
    }

    // 自定义状态码
    public R code(Integer code) {
        this.setCode(code);
        return this;
    }

    // 自定义返回结果
    public R success(Boolean success) {
        this.setSuccess(success);
        return this;
    }
}

