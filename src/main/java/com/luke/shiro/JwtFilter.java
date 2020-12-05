package com.luke.shiro;

import cn.hutool.json.JSONUtil;
import com.luke.common.lang.Result;
import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description 自定义的JwtFileter过滤器
 * @Author luke
 * @Date 2020/11/30 14:40
 */
@Component
public class JwtFilter extends AuthenticatingFilter {

    @Autowired
    JwtUtils jwtUtils;
    /**
     * 实现登陆，生成自定义的JwtToken
     * @param servletRequest
     * @param servletResponse
     * @return
     * @throws Exception
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        String jwt = request.getHeader("Authorization");
        if(StringUtils.isEmpty(jwt)){
            return null;
        }
        return new JwtToken(jwt);
    }

    /**
     *  拦截校验
     *  @description 当头部没有Authorization,直接通过，不需要自动登陆。
     *  当带有Authorization时，需要先校验jwt的时效性，没问题直接执行executeLogin实现自动登陆,将token委托给shiro。
     * @param servletRequest
     * @param servletResponse
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        // 获取用户请求头中的token
        String token = request.getHeader("Authorization");
        if (StringUtils.isEmpty(token)) {// 没有token
            return true;
        } else {
            // 校验jwt
            Claims claim = jwtUtils.getClaimByToken(token);
            // tonken为空或者时间过期
            if (claim == null || jwtUtils.isTokenExpired((claim.getExpiration()))) {
                throw new ExpiredCredentialsException("token以失效，请重新登陆！");
            }
        }
        // 执行自动登陆
        return executeLogin(servletRequest, servletResponse);
    }

    /**
     * 执行登录出现异常
     * @param token
     * @param e
     * @param request
     * @param response
     * @return
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {

        HttpServletResponse httpServletResponse = (HttpServletResponse)response;
        // 1. 判断是否因异常登陆失败
        Throwable throwable = e.getCause() == null ? e : e.getCause();
        // 2.获取登陆异常信息以自定义的Resut响应格式返回json数据
        Result result = Result.fail(throwable.getMessage());
        String json = JSONUtil.toJsonStr(result);// hutool的一个json工具

        // 3.打印响应
        try{
            httpServletResponse.getWriter().print(json);
        } catch (IOException ignored){

        }
        return false;
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {

        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setHeader("Access-Control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个OPTIONS请求，这里我们给OPTIONS请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }

        return super.preHandle(request, response);
    }
}
