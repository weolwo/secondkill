package com.poplar.controller;

import com.poplar.dao.TestDao;
import com.poplar.redis.RedisConfig;
import com.poplar.redis.RedisHelper;
import com.poplar.redis.UserPrefix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * by poplar created on 2020/1/31
 */
@RestController
public class TestController {

    @Autowired
    private TestDao testDao;

    @Autowired
    private RedisHelper redisHelper;

    @RequestMapping("test")
    public Object test() {
        redisHelper.set(new UserPrefix("U"),"id","99999");
        return redisHelper.get(new UserPrefix("U"),"id",Integer.class);
       // return testDao.getById(redisConfig.getPort());
    }
}
