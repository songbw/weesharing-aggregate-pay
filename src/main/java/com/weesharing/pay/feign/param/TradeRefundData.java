package com.weesharing.pay.feign.param;

import com.weesharing.pay.entity.Refund;

import lombok.Data;

@Data
public class TradeRefundData {

	private String outOrderSn;
	private String cardNo;
	private String password;
	private String money;        //单位: 分
	
	public TradeRefundData(Refund refund) {
		super();
		this.outOrderSn = refund.getTradeNo();
		this.cardNo = refund.getCardNo();
		this.password = refund.getCardPwd();
		this.money = refund.getRefundFee();
	}
	
	
}
