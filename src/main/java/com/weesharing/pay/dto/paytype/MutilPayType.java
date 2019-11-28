package com.weesharing.pay.dto.paytype;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class MutilPayType {
	
	@NotNull(message = "appid不能为空")
	private String appid;
	
	@NotNull(message = "支付方式不能为空")
	private String name;
	
	private String desc;

}
