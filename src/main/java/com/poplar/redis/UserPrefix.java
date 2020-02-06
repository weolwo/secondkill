package com.poplar.redis;

/**
 * by poplar created on 2020/2/1
 */
public class UserPrefix extends BasePreFix {

    public static final int TAKEN_EXPIRE = 3600 * 24 * 2;

    public UserPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static UserPrefix taken = new UserPrefix(TAKEN_EXPIRE, "tk");

}
