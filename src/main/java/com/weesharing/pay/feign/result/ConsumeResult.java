package com.weesharing.pay.feign.result;

import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author ZhangPeng
 * @since 2019-09-18
 */
@Data
public class ConsumeResult{

    private Long id;

    /**
     * 订单号
     */
    private String outTradeNo;

    /**
     * 联机账户订单号
     */
    private String tradeNo;

    /**
     * 联机账户卡号
     */
    private String cardNo;

    /**
     * 联机账户密码
     */
    private String cardPwd;

    /**
     * 交易金额
     */
    private String money;

    /**
     * 交易状态
     */
    private Boolean status;

    /**
     * 交易时间
     */
    private String tradeDate;

    /**
     * 前端返回地址
     */
    private String returnUrl;

    /**
     * 异步通知地址
     */
    private String notifyUrl;

}
