package com.poplar.redis;

/**
 * by poplar created on 2020/2/1
 */
public class GoodsDetailKey extends BaseKey {

    public static final int TAKEN_EXPIRE =60;

    public GoodsDetailKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static GoodsDetailKey getGoodsDetail = new GoodsDetailKey(TAKEN_EXPIRE, "gd");

}
