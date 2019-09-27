package com.weesharing.pay.dto.pay;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BalancePay {
	
	@ApiModelProperty(value = "支付方式: pos")
	@NotBlank(message = "支付方式不能为空")
	private String payType;

	@ApiModelProperty(value = "支付订单号")
	@NotBlank(message = "支付订单号不能为空")
	private String orderNo;

	@ApiModelProperty(value = "交易实际金额")
	@NotBlank(message = "交易实际金额不能为空")
	private String actPayFee;

}
