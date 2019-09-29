package com.weesharing.pay.dto.pay;

import javax.validation.constraints.NotBlank;

import com.weesharing.pay.entity.Consume;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BalancePay {
	
	@ApiModelProperty(value = "支付方式: balance")
	@NotBlank(message = "支付方式不能为空")
	private String payType;

	@ApiModelProperty(value = "支付订单号")
	@NotBlank(message = "支付订单号不能为空")
	private String orderNo;

	@ApiModelProperty(value = "交易实际金额")
	@NotBlank(message = "交易实际金额不能为空")
	private String actPayFee;
	
	private String openId;
	
	public Consume convert() {
		Consume consume  = new Consume();
		consume.setPayType(this.payType);
		consume.setOrderNo(this.orderNo);
		consume.setActPayFee(this.actPayFee);
		consume.setCardNo(this.openId);
		return consume;
	}

}
