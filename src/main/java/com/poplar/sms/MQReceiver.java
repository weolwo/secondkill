package com.poplar.sms;

import com.poplar.bean.SedKillOrder;
import com.poplar.bean.User;
import com.poplar.redis.RedisHelper;
import com.poplar.sevice.GoodsService;
import com.poplar.sevice.OrderService;
import com.poplar.sevice.SedKillService;
import com.poplar.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    RedisHelper redisHelper;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SedKillService sedKillService;

    @RabbitListener(queues = MQConfig.SEDKILL_QUEUE)
    public void receive(String message) {
        log.info("receive message:" + message);
        SedKillMessage killMessage = redisHelper.strToObject(message, SedKillMessage.class);
        User user = killMessage.getUser();
        long goodsId = killMessage.getGoodsId();

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if (stock <= 0) {
            return;
        }
        //判断是否已经秒杀到了
        SedKillOrder order = orderService.getSedKillOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return;
        }
        //减库存 下订单 写入秒杀订单
        sedKillService.sedKill(user, goods);
    }

}
