package com.poplar.redis;

/**
 * by poplar created on 2020/2/1
 */
public class AccessKey extends BaseKey {

    private AccessKey(int expireSeconds, String prefix) {

        super(expireSeconds, prefix);
    }

    public static AccessKey withExpire(int expireSeconds) {

        return new AccessKey(expireSeconds, "access");
    }

}
