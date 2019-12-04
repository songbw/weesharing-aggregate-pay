package com.weesharing.pay.dto.notify;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CommonRefundNotify {
	
	private String payType;
	private String refundNo;
	private String orderNo;
	private String refundFee;
	private String tradeNo;
	private String tradeDate;

}
