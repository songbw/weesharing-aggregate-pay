package com.weesharing.pay.feign.param;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Date;

import com.weesharing.pay.entity.Consume;

import cn.hutool.core.date.DateUtil;
import lombok.Data;

@Data
public class WOCConsumeData {
	
	private String cardnum;
	private String password;
	private String phonenum;
	private String paymentamount;
	private String ordernum;
	private String ordertime;
	
	public WOCConsumeData(Consume consume) {
		this.cardnum = consume.getCardNo();
		this.password = consume.getCardPwd();
		this.phonenum = consume.getPayer();
		this.paymentamount = new BigDecimal(consume.getActPayFee()).divide(BigDecimal.valueOf(100)).toString();
		this.ordernum = consume.getOrderNo();
		this.ordertime = DateUtil.format(Date.from(consume.getCreateDate().atZone(ZoneId.systemDefault()).toInstant()), "yyyyMMddHHmmss");
	}
}
