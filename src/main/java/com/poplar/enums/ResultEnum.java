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

    SEDKILL_OVER(5002, "秒杀活动已经结束"),

    REPEAT_SEDKILL(5003, "你已经秒杀过了"),

    USER_NOT_EXIST(600, "用户不存在"),

    USER_NOT_LOGIN(601, "用户不未登录"),

    ORDER_NOT_EXIST(701, "订单不存在"),

    ILLEGALITY_REQUEST(4001, "非法请求"),

    VERIFYCODE_ERROR(4004, "验证码错误"),

    ACCESS_LIMIT_REACHED(6001, "访问达到最大限制次数"),
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
        return new ResultEnvelope(code, message);
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
