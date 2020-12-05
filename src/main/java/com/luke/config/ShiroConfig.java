package com.luke.config;

import com.luke.shiro.AccountRealm;
import com.luke.shiro.JwtFilter;
import com.luke.shiro.RedisManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description shiro配置，注解拦截控制器
 * @Author luke
 * @Date 2020/11/30 12:19
 */
@Configuration
@EnableConfigurationProperties({RedisProperties.class})
public class ShiroConfig {

    @Autowired
    JwtFilter jwtFilter;

    final RedisProperties redis;

    public ShiroConfig(RedisProperties redis) {
        this.redis = redis;
    }

    /**
     * session域管理
     * @return
     */
    @Bean
    public SessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        // 使用自定义的redisSessionDAO
        sessionManager.setSessionDAO(redisSessionDAO());
        return sessionManager;
    }


    /**
     * 重写shiro的安全管理容器，
     * @param accountRealm
     * @param sessionManager
     * @return
     */
    @Bean
    public SessionsSecurityManager securityManager(AccountRealm accountRealm, SessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager(accountRealm);

        //inject sessionManager
        securityManager.setSessionManager(sessionManager);

        // inject redisCacheManager
        securityManager.setCacheManager(cacheManager());
        return securityManager;
    }

    /**
     * 定义过滤器
     * @return
     */
    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition(){
        // 申请一个默认的过滤器链
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        Map<String,String> filterMap = new LinkedHashMap<>();

        //添加一个jwt过滤器到过滤器链中
        filterMap.put("/**","jwt");
        chainDefinition.addPathDefinitions(filterMap);
        return chainDefinition;
    }

    /**
     * 过滤器工厂业务
     * @param securityManager shiro中的安全管理
     * @param shiroFilterChainDefinition
     * @return
     */
    @Bean("shiroFilterFactoryBean")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager,
                                                         ShiroFilterChainDefinition shiroFilterChainDefinition){
        /*shiro过滤器bean对象*/
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);

        // 需要添加的过滤规则
        Map<String, Filter> filters = new HashMap<>();
        filters.put("jwt",jwtFilter);
        shiroFilter.setFilters(filters);


        Map<String,String> filterMap = shiroFilterChainDefinition.getFilterChainMap();
        shiroFilter.setFilterChainDefinitionMap(filterMap);
        return shiroFilter;
    }


    /**
     * cacheManager 缓存 redis实现
     * @return
     */
    public RedisCacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        return redisCacheManager;
    }

    /**
     * 配置shiro redisManager
     * @return
     */
    public RedisManager redisManager() {
        RedisManager redisManager = new RedisManager(redis);
        return redisManager;
    }

    /**
     * RedisSessionDAO shiro sessionDao层的实现
     * 原理就是重写 AbstractSessionDAO
     */
    @Bean
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        return redisSessionDAO;
    }


}
