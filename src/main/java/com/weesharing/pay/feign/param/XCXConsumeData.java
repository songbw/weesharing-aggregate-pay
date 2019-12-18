package com.weesharing.pay.feign.param;

import com.weesharing.pay.entity.Consume;

import lombok.Data;

@Data
public class XCXConsumeData {
	
	private String iAppId;
	private String body;
	private String openId;
	private Integer totalFee;
	private String tradeNo;
	
	public XCXConsumeData() {
	}
	
	public XCXConsumeData(Consume consume) {
		this.body = consume.getBody();
		this.openId = consume.getCardNo();
		this.totalFee = Integer.parseInt(consume.getActPayFee());
		this.tradeNo = consume.getOrderNo();
		this.iAppId = consume.getAppId();
	}
	
}
