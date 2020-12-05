package com.luke.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @Description 定义一个JwtToken来重写该token
 * @Author luke
 * @Date 2020/11/30 14:30
 */
public class JwtToken implements AuthenticationToken {
    private String token;
    public JwtToken(String token){
        this.token = token;
    }
    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
