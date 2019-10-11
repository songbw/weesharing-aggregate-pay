package com.weesharing.pay.dto;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BankAuthBean {
	
	@ApiModelProperty(value = "支付订单号")
	@NotBlank(message = "支付订单号不能为空")
	private String orderNo;
	@ApiModelProperty(value = "银行卡号", example = "")
	private String accountId;
	@ApiModelProperty(value = "姓名", example = "")
	private String accountName;
	@ApiModelProperty(value = "身份证号", example = "")
	private String certNo;
	@ApiModelProperty(value = "CVV2", example = "")
	private String cvv2;
	@ApiModelProperty(value = "是否保存信息: 0:不保存, 1:保存", example = "")
	private String doSaveIt;
	@ApiModelProperty(value = "信用卡有效日期:MMYY", example = "")
	private String expiredDate;
	@ApiModelProperty(value = "电话号码", example = "")
	private String mobileNo;
	@ApiModelProperty(value = "用户代码", example = "")
	private String openId;
	@ApiModelProperty(value = "交易金额", example = "")
	private String tranAmt;  //分

}
