package com.weesharing.pay.feign.param;

import com.weesharing.pay.entity.Consume;
import lombok.Data;

/**
 * 支付宝jsSdk支付
 * 与AlipayConsumeData结构相同
 * */

@Data
public class AliPayJsSdkConsumeData {

    private String iAppId;
    private String subject;
    private Integer totalAmount;
    private String tradeNo;
    private String returnUrl;

    public AliPayJsSdkConsumeData() {
    }

    public AliPayJsSdkConsumeData(Consume consume) {
        this.subject = consume.getBody();
        this.totalAmount = Integer.parseInt(consume.getActPayFee());
        this.tradeNo = consume.getOrderNo();
        this.iAppId = consume.getAppId();
        this.returnUrl = consume.getReturnUrl();
    }

}

