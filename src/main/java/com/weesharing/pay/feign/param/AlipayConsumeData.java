package com.weesharing.pay.feign.param;

import com.weesharing.pay.entity.Consume;

import lombok.Data;

@Data
public class AlipayConsumeData {
	
	private String iAppId;
	private String subject;
	private Integer totalAmount;
	private String tradeNo;
	
	public AlipayConsumeData() {
	}
	
	public AlipayConsumeData(Consume consume) {
		this.subject = consume.getBody();
		this.totalAmount = Integer.parseInt(consume.getActPayFee());
		this.tradeNo = consume.getOrderNo();
		this.iAppId = consume.getAppId();
	}
	
}
