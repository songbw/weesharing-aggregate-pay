package com.weesharing.pay.feign.param;

import com.weesharing.pay.dto.pay.WOAPay;

import lombok.Data;

@Data
public class WOAConsumeData {

	private String outOrderSn;
	private String cardNo;
	private String password;
	private String money;        //单位: 分
	private String returnUrl;
	private String notifyUrl;
	
	public WOAConsumeData(WOAPay pay) {
		this.outOrderSn = pay.getOrderNo();
		this.cardNo = pay.getCardNo();
		this.password = pay.getCardPwd();
		this.money = pay.getActPayFee();
	}
}
