package com.poplar.redis;

/**
 * by poplar created on 2020/2/1
 */
public class OrderPrefix extends BasePreFix {

    public static final int TAKEN_EXPIRE =0;

    public OrderPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static OrderPrefix getByUserIdAndGoodsId = new OrderPrefix(TAKEN_EXPIRE, "ug");

}
