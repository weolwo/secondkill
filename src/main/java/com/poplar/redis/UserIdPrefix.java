package com.poplar.redis;

/**
 * by poplar created on 2020/2/1
 */
public class UserIdPrefix extends BasePreFix {

    public static final int TAKEN_EXPIRE =0;//永久缓存

    public UserIdPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static UserIdPrefix getByUserId = new UserIdPrefix(TAKEN_EXPIRE, "id");

}
