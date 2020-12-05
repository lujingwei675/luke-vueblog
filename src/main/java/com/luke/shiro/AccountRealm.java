package com.luke.shiro;

import com.luke.entity.AccountProfile;
import com.luke.entity.User;
import com.luke.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description shiro配置
 * @Author luke
 * @Date 2020/11/30 12:28
 */
@Slf4j
@Component
public class AccountRealm extends AuthorizingRealm {
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    IUserService userService;

    /**
     * 判断是否为jwt的token
     * @param token
     * @return
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 权限验证
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    /**
     * 登陆认证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // 将传入的AuthenticationToken强转JwtToken
        JwtToken jwtToken = (JwtToken) authenticationToken;
        // 获取jwtToken中的userId
        String userId = jwtUtils.getClaimByToken((String) jwtToken.getPrincipal()).getSubject();
        // 根据jwtToken中的userId查询数据库
        User user = userService.getById(Long.valueOf(userId));
        if(user == null){
            throw new UnknownAccountException("账户不存在！");
        }
        if(user.getStatus() == -1){
            throw new LockedAccountException("账户已被锁定！");
        }
        // 将可以显示的信息放在该载体中，对于密码这种隐秘信息不需要放在该载体中
        AccountProfile accountProfile = new AccountProfile();
        BeanUtils.copyProperties(user,accountProfile);
        log.info("jwt------------->{}",jwtToken);
        // 将token中用户的基本信息返回给shiro
        return new SimpleAuthenticationInfo(accountProfile,jwtToken.getCredentials(),getName());
    }
}
