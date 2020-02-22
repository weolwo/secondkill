package com.poplar.redis;

/**
 * by poplar created on 2020/2/1
 */
public class GoodsListKey extends BaseKey {

    public static final int TAKEN_EXPIRE =60;

    public GoodsListKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static GoodsListKey getGoodsList = new GoodsListKey(TAKEN_EXPIRE, "gl");

    public static GoodsListKey getGoodsStock = new GoodsListKey(0, "gs");

}
