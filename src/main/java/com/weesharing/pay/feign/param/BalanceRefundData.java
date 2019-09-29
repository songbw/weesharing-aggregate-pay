package com.weesharing.pay.feign.param;

import com.weesharing.pay.entity.Refund;

import lombok.Data;

@Data
public class BalanceRefundData {

    private String openId;
    private String refundNo;
    private String orderNo;
    private Integer saleAmount;
    
    public BalanceRefundData() {
	}
    
	public BalanceRefundData(Refund refund) {
		this.openId = refund.getCardNo();
		this.orderNo = refund.getOrderNo();
		this.refundNo = refund.getOutRefundNo();
		this.saleAmount = Integer.parseInt(refund.getRefundFee());
	}

}