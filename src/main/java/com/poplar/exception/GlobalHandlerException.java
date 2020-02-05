package com.poplar.exception;

import com.poplar.enums.ResultEnum;
import com.poplar.util.ResultEnvelope;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * by poplar created on 2020/2/5
 */
@ControllerAdvice
@ResponseBody
public class GlobalHandlerException {

    @ExceptionHandler(value = Exception.class)
    public ResultEnvelope defaultExceptionHandler(HttpServletRequest request, Exception ex) {
        if (ex instanceof GlobalException) {
            GlobalException ge = (GlobalException) ex;
            return new ResultEnvelope(ge.getCode(), ge.getMessage());
        } else if (ex instanceof BindException) {
            //org.springframework.validation.BindException;
            BindException be = (BindException) ex;
            List<ObjectError> allErrors = be.getAllErrors();
            ObjectError error = allErrors.get(0);
            return ResultEnum.BIND_ERROR.fillArgs(error.getDefaultMessage());
        }
        return new ResultEnvelope(ResultEnum.SERVER_ERROR);
    }
}
