package com.poplar.redis;

/**
 * by poplar created on 2020/2/1
 */
public class BasePreFix implements KeyPrefix {

    private int expireSeconds;

    private String prefix;

    public BasePreFix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    public BasePreFix(String prefix) {//0表示永不过期
        this(0,prefix);
    }
    public BasePreFix(int expireSeconds) {
        this.expireSeconds = expireSeconds;
    }

    @Override
    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className+":"+prefix;
    }

    @Override
    public int expireSeconds() {
        return expireSeconds;
    }
}
