package com.poplar.controller;

import com.poplar.sevice.UserService;
import com.poplar.util.ResultEnvelope;
import com.poplar.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * by poplar created on 2020/1/31
 */
@RequestMapping(value = "/login")
@Controller
public class UserController {

    private static Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public ResultEnvelope do_login(@Valid LoginVo loginVo) {
        log.info(loginVo.toString());
        if (userService.login(loginVo)) {
            return ResultEnvelope.success("登录成功");
        }
        return ResultEnvelope.failure();
    }
}
