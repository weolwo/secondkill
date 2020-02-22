package com.poplar.redis;

/**
 * by poplar created on 2020/2/1
 */
public class UserKey extends BaseKey {

    public static final int TAKEN_EXPIRE = 3600 * 24 * 15;

    public UserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static UserKey token = new UserKey(TAKEN_EXPIRE, "tk");

}
