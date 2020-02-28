package com.weesharing.pay.feign.param;

import java.math.BigDecimal;

import com.weesharing.pay.config.AggPayConfig;
import com.weesharing.pay.entity.Refund;
import com.weesharing.pay.feign.BeanContext;

import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class YunChengRefundData {
	
	private final static AggPayConfig aggPayConfig = BeanContext.getBean(AggPayConfig.class);

	private String merchantNo;
	private String refundAmount; //单位: 元
	private String refundNo;
	private String orderNo ;
	private String paymentNo ;
	private String notifyUrl ;
	
	public YunChengRefundData(Refund refund) {
		log.info("YunChengConsumeData: {}",  JSONUtil.wrap(aggPayConfig, false).toString());
		this.merchantNo = aggPayConfig.getIcloudcity();
		this.refundNo = refund.getOutRefundNo();
		this.refundAmount =new BigDecimal(refund.getRefundFee()).divide(BigDecimal.valueOf(100)).setScale(2,BigDecimal.ROUND_HALF_EVEN).toString();
		this.orderNo = refund.getOrderNo();
		this.notifyUrl = aggPayConfig.getIcloudcityRefundNotifyUrl();
	}
	
}
