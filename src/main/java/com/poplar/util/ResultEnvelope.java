package com.poplar.util;

import com.poplar.enums.ResultEnum;
import lombok.Data;

/**
 * by poplar created on 2020/2/3
 */
@Data
public class ResultEnvelope<T> {

    private Integer code;

    private String message;

    private T data;

    public ResultEnvelope(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResultEnvelope(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public ResultEnvelope(ResultEnum resultEnum) {
        this.code = resultEnum.getCode();
        this.message = resultEnum.getMessage();
    }

    public static <T> ResultEnvelope<T> success() {
        return new ResultEnvelope<>(ResultEnum.SUCCESS);
    }

    public static <T> ResultEnvelope<T> success(ResultEnum resultEnum) {
        return new ResultEnvelope<>(resultEnum);
    }

    public static <T> ResultEnvelope<T> success(T data) {
        return new ResultEnvelope<>(0,data);
    }

    public static <T> ResultEnvelope<T> failure() {
        return new ResultEnvelope<>(ResultEnum.FAILURE);
    }

    public static <T> ResultEnvelope<T> failure(ResultEnum resultEnum) {
        return new ResultEnvelope<>(resultEnum);
    }

    public static <T> ResultEnvelope<T> failure(Integer code, String message) {
        return new ResultEnvelope<>(code, message);
    }
}
