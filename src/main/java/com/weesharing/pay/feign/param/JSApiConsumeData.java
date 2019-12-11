package com.weesharing.pay.feign.param;

import com.weesharing.pay.entity.Consume;

import lombok.Data;

@Data
public class JSApiConsumeData {

	private String body;
	private String openId;
	private Integer totalFee;
	private String tradeNo;
	
	public JSApiConsumeData() {
	}
	
	public JSApiConsumeData(Consume consume) {
		this.body = consume.getBody();
		this.openId = consume.getCardNo();
		this.totalFee = Integer.parseInt(consume.getActPayFee());
		this.tradeNo = consume.getOrderNo();
	}
	
}
