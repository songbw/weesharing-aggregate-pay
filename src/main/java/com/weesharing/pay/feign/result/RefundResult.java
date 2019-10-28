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
public class RefundResult {

    /**
     * 主键ID
     */
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
     * 联机账户退款号
     */
    private String refundNo;

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
     * 退款状态
     */
    private Integer status;

    /**
     * 退款时间
     */
    private String tradeDate;

}
