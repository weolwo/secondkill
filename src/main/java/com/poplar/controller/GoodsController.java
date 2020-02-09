package com.poplar.controller;

import com.poplar.bean.User;
import com.poplar.sevice.GoodsService;
import com.poplar.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = "/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("/to_list")
    public String to_list(Model model, User user) {
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsVoList);
        return "goods_list";
    }

    @GetMapping("/to_detail/{id}")
    public String to_detail(Model model, User user, @PathVariable("id") Long id) {
        final GoodsVo goods = goodsService.getGoodsVoByGoodsId(id);
        model.addAttribute("user", user);
        model.addAttribute("goods", goods);
        int status = 0;
        int remainTime = 0;
        long startTime = goods.getStartDate().getTime();
        long endTime = goods.getEndDate().getTime();
        long nowTime = System.currentTimeMillis();
        if (nowTime < startTime) {
            status = 0;//秒杀还没开始
            //倒计时
            remainTime = (int) (startTime - nowTime) / 1000;
        } else if (nowTime > startTime) {
            status = 1;//秒正在秒杀
        } else {
            status = 2;//秒杀结束
        }
        model.addAttribute("status", status);
        model.addAttribute("remainTime", remainTime);
        return "goods_detail";
    }
}
