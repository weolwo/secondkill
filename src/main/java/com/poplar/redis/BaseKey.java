package com.poplar.redis;

/**
 * by poplar created on 2020/2/1
 */
public class BaseKey implements KeyPrefix {

    private int expireSeconds;

    private String prefix;

    public BaseKey(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    public BaseKey(String prefix) {//0表示永不过期
        this(0,prefix);
    }
    public BaseKey(int expireSeconds) {
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
