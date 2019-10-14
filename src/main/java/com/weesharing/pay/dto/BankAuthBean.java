package com.weesharing.pay.dto;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BankAuthBean {
	
	@ApiModelProperty(value = "支付订单号")
	@NotBlank(message = "支付订单号不能为空")
	private String orderNo;
	@ApiModelProperty(value = "银行卡号", example = "6226223329441365")
	private String accountId;
	@ApiModelProperty(value = "姓名", example = "张三")
	private String accountName;
	@ApiModelProperty(value = "身份证号", example = "1xxxxxxxxxxxxxxxxx")
	private String certNo;
	@ApiModelProperty(value = "CVV2", example = "123")
	private String cvv2;
	@ApiModelProperty(value = "是否保存信息: 0:不保存, 1:保存", example = "0")
	private String doSaveIt;
	@ApiModelProperty(value = "银行卡类型", example = "1")
	private String accountType;
	@ApiModelProperty(value = "信用卡有效日期:MMYY", example = "1299")
	private String expiredDate;
	@ApiModelProperty(value = "电话号码", example = "12345678901")
	private String mobileNo;
	@ApiModelProperty(value = "用户代码", example = "123456")
	private String openId;
	@ApiModelProperty(value = "交易金额", example = "11")
	private String tranAmt;  //分

}
