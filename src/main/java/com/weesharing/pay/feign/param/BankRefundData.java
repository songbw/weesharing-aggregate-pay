package com.weesharing.pay.feign.param;

import java.math.BigDecimal;

import com.weesharing.pay.entity.Refund;

import lombok.Data;

@Data
public class BankRefundData {

	private BigDecimal refundAmount;
	private String tranFlow;
	
	public BankRefundData(Refund refund) {
		this.tranFlow = refund.getTradeNo();
		this.refundAmount =new BigDecimal(refund.getRefundFee()).divide(BigDecimal.valueOf(100)).setScale(2,BigDecimal.ROUND_HALF_EVEN);
	}
	
}
