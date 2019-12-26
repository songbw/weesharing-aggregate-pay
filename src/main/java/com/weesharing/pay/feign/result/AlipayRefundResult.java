package com.weesharing.pay.feign.result;

import lombok.Data;

@Data
public class AlipayRefundResult {
	
	private Integer refundFee;
	private String refundNo;
	
}