package com.weesharing.pay.dto.pay;

import com.weesharing.pay.entity.Consume;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class BankPay extends BasePayBean{
	
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
		consume.setPayType(this.getPayType());
		consume.setOrderNo(this.getOrderNo());
		consume.setActPayFee(this.getActPayFee());
		consume.setCardNo(this.getAccountId());
		consume.setCardPwd(this.getCvv2());
		consume.setPayer(this.getMobileNo());
		return consume;
	}
	
}
