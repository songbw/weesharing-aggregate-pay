package com.weesharing.pay.feign.result;

import lombok.Data;

@Data
public class WOCRefundResult {
	
	private String cardnum;
	private String ordernum;
	private String refundtransnum;
	private String transtime;
	
	public WOCRefundResult() {
	}
	
}
