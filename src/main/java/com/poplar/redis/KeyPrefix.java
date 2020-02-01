package com.poplar.redis;

/**
 * by poplar created on 2020/2/1
 */
public interface KeyPrefix {

    String getPrefix();

    int expireSeconds();
}
