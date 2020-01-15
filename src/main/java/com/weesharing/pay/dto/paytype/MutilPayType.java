package com.weesharing.pay.dto.paytype;

import javax.validation.constraints.NotNull;

import com.weesharing.pay.entity.PayTypeEntity;

import lombok.Data;

@Data
public class MutilPayType {
	
	@NotNull(message = "appid不能为空")
	private String appid;
	
	private Long id;
	
	@NotNull(message = "支付方式不能为空")
	private String name;
	
	private String desc;
	
	public PayTypeEntity convert() {
		PayTypeEntity entity = new PayTypeEntity();
		entity.setId(this.id);
		entity.setAppid(this.appid);
		entity.setName(this.name);
		entity.setDescription(this.desc);
		return entity;
	}

}
