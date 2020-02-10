package com.poplar.sevice;

import com.poplar.bean.OrderInfo;
import com.poplar.bean.SedKillGoods;
import com.poplar.bean.SedKillOrder;
import com.poplar.bean.User;
import com.poplar.dao.OrderDao;
import com.poplar.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * by poplar created on 2020/2/9
 */
@Service
public class SedKillService {

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @Transactional
    public OrderInfo sedKill(User user, GoodsVo goods) {
        //减库存，创建订单
        goodsService.reduceStock(goods.getId());
        return orderService.createOrder(user, goods);
    }

}
