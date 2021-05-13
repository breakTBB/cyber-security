package com.example.demo.controller;

import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class userController {
    @Autowired
    private UserService userService;

    @PostMapping(value = "/login")
    public String login(@RequestParam("username") String username,
                      @RequestParam("pwd") String pwd) {
        if (userService.login(username, pwd)) {
            return "验证成功";
        }
        return "验证失败";
    }

    @PostMapping(value = "/register")
    public String register(@RequestParam("username") String username,
                           @RequestParam("pwd") String pwd) {
        if (userService.register(username, pwd)) {
            return "注册成功";
        }
        return "注册失败";
    }
}
