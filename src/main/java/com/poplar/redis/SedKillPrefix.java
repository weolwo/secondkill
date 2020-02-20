package com.poplar.redis;

/**
 * by poplar created on 2020/2/1
 */
public class SedKillPrefix extends BasePreFix {

    public static final int TAKEN_EXPIRE = 0;

    public SedKillPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static SedKillPrefix isGoodsOver = new SedKillPrefix(TAKEN_EXPIRE, "go");

}
