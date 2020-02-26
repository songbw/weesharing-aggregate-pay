package com.weesharing.pay.feign.param;

import java.math.BigDecimal;

import com.weesharing.pay.config.AggPayConfig;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.feign.BeanContext;

import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class YunChengConsumeData {
	
	private final static AggPayConfig aggPayConfig = BeanContext.getBean(AggPayConfig.class);
	
	private String merchantNo;
	private String tradeOrderNo;
	private BigDecimal amount;
	private Integer expireTimeMinute;
	private String notifyUrl;
	private String goodsDesc;
	
	
	public YunChengConsumeData() {
	}
	
	public YunChengConsumeData(Consume consume) {
		log.info("YunChengConsumeData: {}",  JSONUtil.wrap(aggPayConfig, false).toString());
		this.merchantNo = aggPayConfig.getIcloudcity();
		this.tradeOrderNo = consume.getOrderNo();
		this.amount = new BigDecimal(consume.getActPayFee()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_EVEN);
		this.expireTimeMinute = 30;
		this.notifyUrl = aggPayConfig.getIcloudcityNotifyUrl();
		this.goodsDesc = consume.getBody();
	}
	
}
