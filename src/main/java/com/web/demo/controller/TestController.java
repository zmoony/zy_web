package com.web.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yuez
 * @title: TestController
 * @projectName zy_web
 * @description: 测试
 * @date 2020/9/2 14:20
 */
@RestController
public class TestController {

    @GetMapping("/date")
    public Map<String,Object> dateFormat (){
        return new HashMap<String,Object>(){
            {
                put("date",new Date());
                put("localdate", LocalDateTime.now());
            }
        };
    }
    @PostMapping("/date")
    public Map<String,Object> dateFormat2 (){
        return new HashMap<String,Object>(){
            {
                put("date",1);
                put("localdate", LocalDateTime.now());
            }
        };
    }
}
