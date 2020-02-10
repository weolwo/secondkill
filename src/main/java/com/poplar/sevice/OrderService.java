package com.poplar.sevice;

import com.poplar.bean.OrderInfo;
import com.poplar.bean.SedKillOrder;
import com.poplar.bean.User;
import com.poplar.dao.OrderDao;
import com.poplar.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * by poplar created on 2020/2/9
 */
@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    public SedKillOrder getSedKillOrderByUserIdGoodsId(Long userId, Long goodsId) {
        return orderDao.getSedKillOrderByUserIdGoodsId(userId, goodsId);
    }

    @Transactional
    public OrderInfo createOrder(User user, GoodsVo goods) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setDeliveryAddrId(9L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getPrice());
        orderInfo.setOrderChannel(88);
        orderInfo.setCreateDate(new Date());
        orderInfo.setStatus(0);//未支付
        orderInfo.setUserId(user.getId());
        long orderId = orderDao.insert(orderInfo);

        SedKillOrder sedKillOrder = new SedKillOrder();
        sedKillOrder.setUserId(user.getId());
        sedKillOrder.setGoodsId(goods.getId());
        sedKillOrder.setOrderId(orderId);
        orderDao.insertSedKillOrder(sedKillOrder);
        return orderInfo;
    }
}
