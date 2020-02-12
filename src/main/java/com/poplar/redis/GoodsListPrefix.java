package com.poplar.redis;

/**
 * by poplar created on 2020/2/1
 */
public class GoodsListPrefix extends BasePreFix {

    public static final int TAKEN_EXPIRE =60;

    public GoodsListPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static GoodsListPrefix getGoodsList = new GoodsListPrefix(TAKEN_EXPIRE, "gl");

}
