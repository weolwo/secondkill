package com.poplar.redis;

/**
 * by poplar created on 2020/2/1
 */
public class UserIdKey extends BaseKey {

    public static final int TAKEN_EXPIRE =0;//永久缓存

    public UserIdKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static UserIdKey getByUserId = new UserIdKey(TAKEN_EXPIRE, "id");

}
