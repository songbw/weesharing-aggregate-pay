package com.weesharing.pay.dto;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 支付对象
 * </p>
 *
 * @author ZhangPeng
 * @since 2019-09-18
 */
@Data
@ApiModel(value = "支付对象", description = "")
public class PayDTO {

	@ApiModelProperty(value = "支付方式")
	@NotBlank(message = "支付方式不能为空")
	private String payType;

	@ApiModelProperty(value = "支付订单号")
	@NotBlank(message = "支付订单号不能为空")
	private String orderNo;

	@ApiModelProperty(value = "交易实际金额")
	@NotBlank(message = "交易实际金额不能为空")
	private String actPayFree;

	@ApiModelProperty(value = "联机账户卡号")
	@NotBlank(message = "联机账户卡号不能为空")
	private String cardNo;

	@ApiModelProperty(value = "联机账户密码")
	@NotBlank(message = "联机账户密码不能为空")
	private String cardPwd;

}
