package com.poplar.enums;

import com.poplar.util.ResultEnvelope;

/**
 * by poplar created on 2020/2/4
 */
public enum ResultEnum {

    UNKNOWN_ERROR(-1, "未知异常"),

    SUCCESS(0, "success"),

    FAILURE(1, "失败"),

    BIND_ERROR(4001, "参数校验异常：%s"),

    SERVER_ERROR(5001, "服务器异常"),
    ;

    private Integer code;

    private String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResultEnvelope fillArgs(Object... args) {
        int code = this.code;
        String message = String.format(this.message, args);
        return new ResultEnvelope(code,message);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
