package com.luke.util;

import com.luke.entity.AccountProfile;
import org.apache.shiro.SecurityUtils;

/**
 * @Description Shiro相关工具类
 * @Author luke
 * @Date 2020/12/3 15:55
 */
public class ShiroUtil {

    public static AccountProfile getProfile() {
        return (AccountProfile) SecurityUtils.getSubject().getPrincipal();
    }

}
