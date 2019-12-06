package com.weesharing.pay.feign.result;

import lombok.Data;

@Data
public class XCXPayResult {
	
	private String nonceStr;
	private String packageStr;
	private String paySign;
	private String timeStamp;
	private String signType;
	
}