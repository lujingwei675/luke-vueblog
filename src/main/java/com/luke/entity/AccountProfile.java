package com.luke.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description 编写登陆成功返回用户信息的载体AccountProfile
 * @Author luke
 * @Date 2020/11/30 14:35
 */
@Data
public class AccountProfile implements Serializable {
    private Long id;
    private String username;
    private String avatar;
    private String email;
}
