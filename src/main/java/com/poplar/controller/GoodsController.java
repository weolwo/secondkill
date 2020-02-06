package com.poplar.controller;

import com.poplar.bean.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/goods")
public class GoodsController {

    @GetMapping("/to_list")
    public String greeting(Model model, User user) {
        model.addAttribute("user", user);
        return "goods_list";
    }

}
