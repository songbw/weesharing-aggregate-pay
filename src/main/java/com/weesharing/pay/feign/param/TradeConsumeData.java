package com.weesharing.pay.feign.param;

import com.weesharing.pay.dto.PayDTO;

import lombok.Data;

@Data
public class TradeConsumeData {

	private String outOrderSn;
	private String cardNo;
	private String password;
	private String money;        //单位: 分
	private String returnUrl;
	private String notifyUrl;
	
	public TradeConsumeData(PayDTO pay) {
		this.outOrderSn = pay.getOrderNo();
		this.cardNo = pay.getCardNo();
		this.password = pay.getCardPwd();
		this.money = pay.getActPayFree();
	}
}
