package com.poplar.controller;

import com.poplar.bean.OrderInfo;
import com.poplar.bean.SedKillOrder;
import com.poplar.bean.User;
import com.poplar.enums.ResultEnum;
import com.poplar.sevice.GoodsService;
import com.poplar.sevice.OrderService;
import com.poplar.sevice.SedKillService;
import com.poplar.util.ResultEnvelope;
import com.poplar.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * by poplar created on 2020/2/9
 */
@Controller
@RequestMapping(value = "/sedKill")
public class SedKillController {

    @Autowired
    private SedKillService sedKillService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    /*QPS 692 50000*/
    @PostMapping(value = "/do_sedKill")
    @ResponseBody
    public ResultEnvelope<OrderInfo> do_sedKill(@RequestParam("goodsId") Long goodsId, User user) {
        if (user == null) {
            return ResultEnvelope.failure(ResultEnum.USER_NOT_LOGIN);
        }
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        //判断商品库存
        int stock = goodsService.getStockCount(goodsId);
        if (stock <= 0) {
            return ResultEnvelope.failure(ResultEnum.SEDKILL_OVER);
        }
        //判断是否重复秒杀
        SedKillOrder order = orderService.getSedKillOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return ResultEnvelope.failure(ResultEnum.REPEAT_SEDKILL);
        }
        //下订单，写入库存
        OrderInfo orderInfo = sedKillService.sedKill(user, goodsVo);
        return ResultEnvelope.success(orderInfo);
    }

    /*QPS 692 50000*/
    @PostMapping(value = "/do_sedKill2")
    public String do_sedKill2(Model model, @RequestParam("goodsId") Long goodsId, User user) {
        if (user == null) {
            return "login";
        }
        model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        //判断商品库存
        int stock = goodsService.getStockCount(goodsId);
        if (stock <= 0) {
            model.addAttribute("error", ResultEnum.SEDKILL_OVER.getMessage());
            return "fail";
        }
        //判断是否重复秒杀
        SedKillOrder order = orderService.getSedKillOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            model.addAttribute("error", ResultEnum.REPEAT_SEDKILL.getMessage());
            return "fail";
        }
        //下订单，写入库存
        OrderInfo orderInfo = sedKillService.sedKill(user, goodsVo);
        model.addAttribute("goods", goodsVo);
        model.addAttribute("orderInfo", orderInfo);
        return "order_detail";
    }
}
