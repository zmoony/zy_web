package com.web.demo.init;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author yuez
 * @title: ServiceProperties
 * @projectName zy_web
 * @description: 读取配置文件
 * @date 2020/8/29 11:24
 */
@Slf4j
@ConfigurationProperties(prefix = "service.bigdata",ignoreUnknownFields=true)
@Configuration
@Component
@Data
public class ServiceBigdataProperties {
    private String url;
}
