package com.poplar.redis;

/**
 * by poplar created on 2020/2/1
 */
public class UserPrefix extends BasePreFix {

    public UserPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public UserPrefix(String prefix) {
        super(prefix);
    }

    public UserPrefix(int expireSeconds) {
        super(expireSeconds);
    }
}
