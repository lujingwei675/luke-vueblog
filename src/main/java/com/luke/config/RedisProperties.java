package com.luke.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @Description 自定义redis配置
 * @Author luke
 * @Date 2020/12/4 11:12
 */
@Data
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {

    private String host;
    private int port;
    private int timeout;
    private String password;
    private int database;

}
