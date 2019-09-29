package com.weesharing.pay.dto.pay;

import javax.validation.constraints.NotBlank;

import com.weesharing.pay.entity.Consume;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WOAPay {
	@ApiModelProperty(value = "支付方式: pos")
	@NotBlank(message = "支付方式不能为空")
	private String payType;

	@ApiModelProperty(value = "支付订单号")
	@NotBlank(message = "支付订单号不能为空")
	private String orderNo;

	@ApiModelProperty(value = "交易实际金额")
	@NotBlank(message = "交易实际金额不能为空")
	private String actPayFee;

	@ApiModelProperty(value = "联机账户卡号")
	@NotBlank(message = "联机账户卡号不能为空")
	private String cardNo;

	@ApiModelProperty(value = "联机账户密码")
	@NotBlank(message = "联机账户密码不能为空")
	private String cardPwd;
	
	public Consume convert() {
		Consume consume  = new Consume();
		consume.setPayType(this.payType);
		consume.setOrderNo(this.orderNo);
		consume.setActPayFee(this.actPayFee);
		consume.setCardNo(this.cardNo);
		consume.setCardPwd(this.cardPwd);
		return consume;
	}
}
