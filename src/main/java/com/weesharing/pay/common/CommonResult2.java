package com.weesharing.pay.common;

import lombok.Data;

/**
 * 通用返回对象
 * Created by macro on 2019/4/19.
 */
@Data
public class CommonResult2<T> {
    private long code;
    private String msg;
    private T data;

    protected CommonResult2() {
    }

    protected CommonResult2(long code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> CommonResult2<T> success() {
        return success(null);
    }
    
    public static <T> CommonResult2<T> success(T data) {
        return new CommonResult2<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     * @param  msg 提示信息
     */
    public static <T> CommonResult2<T> success(T data, String msg) {
        return new CommonResult2<T>(ResultCode.SUCCESS.getCode(), msg, data);
    }

    /**
     * 失败返回结果
     * @param errorCode 错误码
     */
    public static <T> CommonResult2<T> failed(IErrorCode errorCode) {
        return new CommonResult2<T>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    /**
     * 失败返回结果
     * @param msg 提示信息
     */
    public static <T> CommonResult2<T> failed(String msg) {
        return new CommonResult2<T>(ResultCode.FAILED.getCode(), msg, null);
    }

    /**
     * 失败返回结果
     */
    public static <T> CommonResult2<T> failed() {
        return failed(ResultCode.FAILED);
    }

    /**
     * 参数验证失败返回结果
     */
    public static <T> CommonResult2<T> validateFailed() {
        return failed(ResultCode.VALIDATE_FAILED);
    }

    /**
     * 参数验证失败返回结果
     * @param msg 提示信息
     */
    public static <T> CommonResult2<T> validateFailed(String msg) {
        return new CommonResult2<T>(ResultCode.VALIDATE_FAILED.getCode(), msg, null);
    }

    /**
     * 未登录返回结果
     */
    public static <T> CommonResult2<T> unauthorized(T data) {
        return new CommonResult2<T>(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage(), data);
    }

    /**
     * 未授权返回结果
     */
    public static <T> CommonResult2<T> forbidden(T data) {
        return new CommonResult2<T>(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage(), data);
    }
  
}
