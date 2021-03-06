package com.weesharing.pay.feign.param;

import com.weesharing.pay.entity.Refund;

import lombok.Data;

@Data
public class WOARefundData {

	private String outOrderSn;
	private String cardNo;
	private String password;
	private String money;        //单位: 分
	
	public WOARefundData(Refund refund) {
		this.outOrderSn = refund.getOrderNo();
		this.cardNo = refund.getCardNo();
		this.password = refund.getCardPwd();
		this.money = refund.getRefundFee();
	}
	
}
