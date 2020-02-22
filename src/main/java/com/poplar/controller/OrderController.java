package com.poplar.controller;

import com.poplar.bean.OrderInfo;
import com.poplar.bean.User;
import com.poplar.enums.ResultEnum;
import com.poplar.sevice.GoodsService;
import com.poplar.sevice.OrderService;
import com.poplar.util.ResultEnvelope;
import com.poplar.vo.GoodsVo;
import com.poplar.vo.OrderDetailVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * by poplar created on 2020/1/31
 */
@RequestMapping(value = "/order")
@Controller
public class OrderController {

    private static Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public ResultEnvelope<OrderDetailVo> detail(@RequestParam("orderId") Long orderId, User user) {
        if (user == null) {
            ResultEnvelope.failure(ResultEnum.USER_NOT_LOGIN);
        }
        OrderInfo orderInfo = orderService.getByOrderId(orderId);
        if (orderInfo == null) {
            return ResultEnvelope.failure(ResultEnum.ORDER_NOT_EXIST);
        }
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(orderInfo.getGoodsId());
        OrderDetailVo detailVo = new OrderDetailVo();
        detailVo.setGoods(goodsVo);
        detailVo.setOrder(orderInfo);
        return ResultEnvelope.success(detailVo);
    }

}
