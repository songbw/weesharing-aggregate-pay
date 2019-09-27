package com.weesharing.pay.dto.pay;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WOCPay {
	
	@ApiModelProperty(value = "支付方式: card")
	@NotBlank(message = "支付方式不能为空")
	private String payType;

	@ApiModelProperty(value = "支付订单号")
	@NotBlank(message = "支付订单号不能为空")
	private String orderNo;

	@ApiModelProperty(value = "交易实际金额")
	@NotBlank(message = "交易实际金额不能为空")
	private String actPayFee;

	@ApiModelProperty(value = "惠民优选卡卡号")
	@NotBlank(message = "惠民优选卡卡号不能为空")
	private String cardNo;

	@ApiModelProperty(value = "惠民优选卡密码")
	private String cardPwd;
	
	@ApiModelProperty(value = "惠民优选卡密码")
	@NotBlank(message = "手机号不能为空")
	private String mobile;

}
