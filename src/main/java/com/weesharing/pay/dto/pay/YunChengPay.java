package com.weesharing.pay.dto.pay;

import javax.validation.constraints.NotBlank;

import com.weesharing.pay.entity.Consume;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class YunChengPay extends BasePayBean{
	
	@ApiModelProperty(value = "商品描述")
	@NotBlank(message = "商品描述")
	private String goodsDesc;
	
	public Consume convert() {
		Consume consume  = new Consume();
		consume.setPayType(this.getPayType());
		consume.setOrderNo(this.getOrderNo());
		consume.setActPayFee(this.getActPayFee());
		consume.setBody(getGoodsDesc());
		return consume;
	}

}
