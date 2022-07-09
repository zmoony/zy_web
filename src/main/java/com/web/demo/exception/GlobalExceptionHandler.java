package com.web.demo.exception;


import com.web.demo.common.R;
import com.web.demo.common.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;

/**
 * 统一异常处理器
 * 使用@ControllerAdvice集成@ExceptionHandler的方法到一个类中；
 * @ControllerAdvice
 * 是一种作用于控制层的切面通知（Advice），该注解能够将通用的@ExceptionHandler、@InitBinder和@ModelAttributes方法收集到一个类型，并应用到所有控制器上
 * Created by zy on 2020/4/20.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**-------- 通用异常处理方法 --------**/
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public R error(Exception e){
        log.error(ExceptionUtil.getMessage(e));
        return R.error();
    }
    /**-------- 指定异常处理方法 --------**/
    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public R error(NullPointerException e){
        log.error(ExceptionUtil.getMessage(e));
        return R.setResult(ResultCodeEnum.NULL_POINT);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseBody
    public R error(HttpClientErrorException e){
        log.error(ExceptionUtil.getMessage(e));
        return R.setResult(ResultCodeEnum.HTTP_CLIENT_ERROR);
    }
    /**-------- 自定义定异常处理方法 --------**/
    @ExceptionHandler(GlobalException.class)
    @ResponseBody
    public R error(GlobalException e){
        log.error(ExceptionUtil.getMessage(e));
        return R.error().message(e.getMessage()).code(e.getCode());
    }


}
