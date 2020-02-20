package com.poplar.sms;

import com.poplar.redis.RedisHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {

    private static Logger log = LoggerFactory.getLogger(MQSender.class);

    @Autowired
    AmqpTemplate amqpTemplate;

    @Autowired
    private RedisHelper redisHelper;

    public void sendSedKillMessage(SedKillMessage mm) {
        String msg = redisHelper.objectToString(mm);
        log.info("send message:{}", msg);
        amqpTemplate.convertAndSend(MQConfig.SEDKILL_QUEUE, msg);
    }
}
