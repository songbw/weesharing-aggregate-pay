package com.weesharing.pay.dto.pay;

import javax.validation.constraints.NotBlank;

import com.weesharing.pay.entity.Consume;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WOCPay extends BasePayBean{
	
	@ApiModelProperty(value = "惠民优选卡卡号")
	@NotBlank(message = "惠民优选卡卡号不能为空")
	private String cardNo;

	@ApiModelProperty(value = "惠民优选卡密码")
	private String cardPwd;
	
	@ApiModelProperty(value = "惠民优选卡密码")
	@NotBlank(message = "手机号不能为空")
	private String mobile;
	
	public Consume convert() {
		Consume consume  = new Consume();
		consume.setPayType(this.getPayType());
		consume.setOrderNo(this.getOrderNo());
		consume.setActPayFee(this.getActPayFee());
		consume.setCardNo(this.getCardNo());
		consume.setCardPwd(this.getCardPwd());
		consume.setPayer(this.getMobile());
		return consume;
	}

}
