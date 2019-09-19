package com.weesharing.pay.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.weesharing.pay.common.CommonResult;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 验证异常
     * @param req
     * @param e
     * @return
     * @throws MethodArgumentNotValidException
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public CommonResult<String> handleMethodArgumentNotValidException(HttpServletRequest req, MethodArgumentNotValidException e) throws MethodArgumentNotValidException {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder errorMesssage = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMesssage.append( "\n" + fieldError.getDefaultMessage());
        }
        log.info("MethodArgumentNotValidException",e.getMessage());
        return CommonResult.failed(errorMesssage.toString());
    }

    /**
     * 全局异常
     * @param req
     * @param e
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public CommonResult<String> handleException(HttpServletRequest req, Exception e) throws Exception {
        log.error("handleException==>{}", e.getMessage());
        return CommonResult.failed(e.getMessage());
    }
}