package com.luke.controller;


import com.luke.common.lang.Result;
import com.luke.entity.User;
import com.luke.service.IUserService;
import com.luke.service.impl.UserServiceImpl;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jobob
 * @since 2020-11-29
 */
@RestController
@RequestMapping("/user")
public class UserController {

    final
    IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @RequiresAuthentication
    @GetMapping("/index")
    public Result index() {
        User user = userService.getById(1L);
        return Result.succ(user);
    }

    @PostMapping("/save")
    public Result save(@Validated @RequestBody User user) {
//        userService.save(user);
        return Result.succ(user);
    }
}
