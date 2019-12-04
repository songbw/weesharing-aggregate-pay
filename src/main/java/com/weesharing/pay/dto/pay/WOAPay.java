package com.weesharing.pay.dto.pay;

import javax.validation.constraints.NotBlank;

import com.weesharing.pay.entity.Consume;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WOAPay extends BasePayBean{
	
	@ApiModelProperty(value = "联机账户卡号")
	@NotBlank(message = "联机账户卡号不能为空")
	private String cardNo;

	@ApiModelProperty(value = "联机账户密码")
	@NotBlank(message = "联机账户密码不能为空")
	private String cardPwd;
	
	public Consume convert() {
		Consume consume  = new Consume();
		consume.setPayType(this.getPayType());
		consume.setOrderNo(this.getOrderNo());
		consume.setActPayFee(this.getActPayFee());
		consume.setCardNo(this.getCardNo());
		consume.setCardPwd(this.getCardPwd());
		return consume;
	}
}
