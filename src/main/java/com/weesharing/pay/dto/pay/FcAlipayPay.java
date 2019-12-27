package com.weesharing.pay.dto.pay;

import com.weesharing.pay.entity.Consume;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FcAlipayPay extends BasePayBean{
	
	private String body;
	private String returnUrl;

	public Consume convert() {
		Consume consume  = new Consume();
		consume.setBody(this.getBody());
		consume.setPayType(this.getPayType());
		consume.setOrderNo(this.getOrderNo());
		consume.setActPayFee(this.getActPayFee());
		consume.setReturnUrl(this.getReturnUrl());
		return consume;
	}
	
}
