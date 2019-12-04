package com.weesharing.pay.dto.pay;

import com.weesharing.pay.entity.Consume;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FcWxXcxPay extends BasePayBean{
	
	public Consume convert() {
		Consume consume  = new Consume();
		consume.setPayType(this.getPayType());
		consume.setOrderNo(this.getOrderNo());
		consume.setActPayFee(this.getActPayFee());
		return consume;
	}

}
