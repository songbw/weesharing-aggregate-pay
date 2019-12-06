package com.weesharing.pay.feign.result;

import lombok.Data;

@Data
public class XCXRefundResult {
	
	private Integer refundFee;
	private String refundNo;
	private String wechatRefundNo;
	
}