package com.weesharing.pay.dto.pay;

import com.weesharing.pay.entity.Consume;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BalancePay extends BasePayBean{
	
	@ApiModelProperty(value = "用户号")
	private String openId;
	
	public Consume convert() {
		Consume consume  = new Consume();
		consume.setPayType(this.getPayType());
		consume.setOrderNo(this.getOrderNo());
		consume.setActPayFee(this.getActPayFee());
		consume.setCardNo(this.getOpenId());
		return consume;
	}

}
