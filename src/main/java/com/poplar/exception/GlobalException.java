package com.poplar.exception;

import com.poplar.enums.ResultEnum;
import lombok.Getter;

/**
 * by poplar created on 2020/2/5
 */
@Getter
public class GlobalException extends RuntimeException {

    private String message;

    private Integer code;

    public GlobalException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());
        this.message = resultEnum.getMessage();
        this.code = resultEnum.getCode();
    }
}
