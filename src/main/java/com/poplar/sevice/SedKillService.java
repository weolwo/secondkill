package com.poplar.sevice;

import com.poplar.bean.OrderInfo;
import com.poplar.bean.SedKillOrder;
import com.poplar.bean.User;
import com.poplar.redis.RedisHelper;
import com.poplar.redis.SedKillPrefix;
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

    @Autowired
    RedisHelper redisHelper;

    @Transactional
    public OrderInfo sedKill(User user, GoodsVo goods) {
        //减库存，创建订单
        Boolean flag = goodsService.reduceStock(goods.getId());
        if (flag) {
            return orderService.createOrder(user, goods);
        } else {
            setGoodsOver(goods.getId());
            return null;
        }
    }

    public Long getSedKillResult(Long userId, Long goodsId) {
        SedKillOrder order = orderService.getSedKillOrderByUserIdGoodsId(userId, goodsId);
        if (order != null) {
            return order.getOrderId();
        } else {
            boolean over = getGoodsOver(goodsId);
            if (over) {
                return -1L;
            } else {
                return 0L;
            }
        }
    }

    private boolean getGoodsOver(long goodsId) {
        return redisHelper.exist(SedKillPrefix.isGoodsOver, "" + goodsId);
    }

    //标记
    private void setGoodsOver(long goodsId) {
        redisHelper.set(SedKillPrefix.isGoodsOver, "" + goodsId, true);
    }
}
