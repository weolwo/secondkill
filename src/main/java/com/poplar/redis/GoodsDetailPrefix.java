package com.poplar.redis;

/**
 * by poplar created on 2020/2/1
 */
public class GoodsDetailPrefix extends BasePreFix {

    public static final int TAKEN_EXPIRE =60;

    public GoodsDetailPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static GoodsDetailPrefix getGoodsDetail = new GoodsDetailPrefix(TAKEN_EXPIRE, "gd");

}
