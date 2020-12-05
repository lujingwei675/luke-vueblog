package com.luke.shiro;

import com.luke.config.RedisProperties;
import org.crazycake.shiro.IRedisManager;
import org.crazycake.shiro.WorkAloneRedisManager;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @Description 重写提供的shiro+redis的RedisManager读取自定义配置
 * @Author luke
 * @Date 2020/12/4 11:09
 */
@Component
public class RedisManager extends WorkAloneRedisManager implements IRedisManager {

    private RedisProperties redisProperties;
    private JedisPool jedisPool;

    public RedisManager(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    private void init() {
        synchronized (this) {
            if (this.jedisPool == null) {
                this.jedisPool = new JedisPool(this.getJedisPoolConfig(), redisProperties.getHost(), redisProperties.getPort(),
                        redisProperties.getTimeout(), redisProperties.getPassword(), redisProperties.getDatabase());
            }
        }
    }

    @Override
    protected Jedis getJedis() {
        if (this.jedisPool == null) {
            this.init();
        }
        return this.jedisPool.getResource();
    }
}
