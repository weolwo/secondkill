package com.poplar.redis;

/**
 * by poplar created on 2020/2/1
 */
public class SedKillKey extends BaseKey {

    public static final int TAKEN_EXPIRE = 0;

    public SedKillKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static SedKillKey isGoodsOver = new SedKillKey(TAKEN_EXPIRE, "go");

    public static SedKillKey getSedKillPath = new SedKillKey(600, "sp");

    public static SedKillKey getSedKillVerifyCode = new SedKillKey(120, "sv");

}
