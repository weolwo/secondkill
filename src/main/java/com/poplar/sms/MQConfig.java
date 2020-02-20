package com.poplar.sms;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MQConfig {

    public static final String SEDKILL_QUEUE = "sedkill_queue";

    /**
     * Direct模式 交换机Exchange
     */
    @Bean
    public Queue queue() {
        return new Queue(SEDKILL_QUEUE, true);
    }

}
