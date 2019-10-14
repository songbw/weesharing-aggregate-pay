package com.weesharing.pay.dto.pay;

import javax.validation.constraints.NotBlank;

import com.weesharing.pay.entity.Consume;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BankPay {
	
	@ApiModelProperty(value = "支付方式: bank", example = "bank")
	@NotBlank(message = "支付方式不能为空")
	private String payType;

	@ApiModelProperty(value = "支付订单号")
	@NotBlank(message = "支付订单号不能为空")
	private String orderNo;

	@ApiModelProperty(value = "交易实际金额", example = "11")
	@NotBlank(message = "交易实际金额不能为空")
	private String actPayFee;
	
	@ApiModelProperty(value = "银行卡号", example = "6226223329441365")
	private String accountId;
	
	@ApiModelProperty(value = "姓名", example = "张三")
	private String accountName;
	
	@ApiModelProperty(value = "身份证号", example = "1xxxxxxxxxxxxxxxxx")
	private String certNo;
	
	@ApiModelProperty(value = "CVV2", example = "123")
	private String cvv2;
	
	@ApiModelProperty(value = "信用卡有效日期:MMYY", example = "1299")
	private String expiredDate;
	
	@ApiModelProperty(value = "电话号码", example = "12345678901")
	private String mobileNo;
	
	@ApiModelProperty(value = "银行卡类型", example = "1")
	private String accountType;
	
	@ApiModelProperty(value = "短信验证码", example = "666666")
	private String verifyCode;

	public Consume convert() {
		
		Consume consume  = new Consume();
		consume.setPayType(this.payType);
		consume.setOrderNo(this.orderNo);
		consume.setActPayFee(this.actPayFee);
		consume.setCardNo(this.accountId);
		consume.setCardPwd(this.getCvv2());
		consume.setPayer(this.getMobileNo());
		return consume;
	}
	
}
