package com.weesharing.pay.feign.param;

import com.weesharing.pay.entity.Refund;

import lombok.Data;

@Data
public class AlipayRefundData {

	private String refundNo;
	private String orderId ;
	private String openId ;
	private Integer totalFee ;
	private Integer refundFee ;        //单位: 分
	
	public AlipayRefundData(Refund refund) {
		this.refundNo = refund.getOutRefundNo();
		this.orderId = refund.getOrderNo();
		this.openId = refund.getCardNo();
		this.totalFee = Integer.parseInt(refund.getTotalFee());
		this.refundFee = Integer.parseInt(refund.getRefundFee());
	}
	
}
