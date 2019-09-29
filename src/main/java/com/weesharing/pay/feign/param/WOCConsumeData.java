package com.weesharing.pay.feign.param;

import com.weesharing.pay.entity.Consume;

import lombok.Data;

@Data
public class WOCConsumeData {
	
	private String cardnum;
	private String password;
	private String phonenum;
	private String paymentamount;
	private String ordernum;
	private String ordertime;
	
	public WOCConsumeData(Consume consume) {
		this.cardnum = consume.getCardNo();
		this.password = consume.getCardPwd();
		this.phonenum = consume.getPayer();
		this.paymentamount = consume.getActPayFee();
		this.ordernum = consume.getOrderNo();
		this.ordertime = consume.getCreateDate().toString();
	}
}
