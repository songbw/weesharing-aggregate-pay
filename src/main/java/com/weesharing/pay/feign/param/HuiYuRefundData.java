package com.weesharing.pay.feign.param;

import com.weesharing.pay.entity.Refund;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class HuiYuRefundData {

    private String refundAmount;
    private String accountNo;
    private String paymentNo;
    private String orderNo;

    public HuiYuRefundData(Refund refund){
        this.refundAmount = refund.getRefundFee();
        this.accountNo = refund.getCardNo();
        this.orderNo = refund.getOrderNo();
    }

}
