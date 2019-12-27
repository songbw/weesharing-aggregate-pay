package com.weesharing.pay.feign.result;

import lombok.Data;

@Data
public class AlipayRefundResult {
	
	private String refundFee;
	private String refundNo;
	private String tradeNo;
	private String aliPayTradeNo;
	private String refundDate;
	private String aliPayLoginId;
	private String buyerId;
	
}