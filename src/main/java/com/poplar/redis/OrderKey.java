package com.poplar.redis;

/**
 * by poplar created on 2020/2/1
 */
public class OrderKey extends BaseKey {

    public static final int TAKEN_EXPIRE =0;

    public OrderKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static OrderKey getByUserIdAndGoodsId = new OrderKey(TAKEN_EXPIRE, "ug");

}
