package com.weesharing.pay.dto.notify;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CommonPayNotify {
	
	private String payType;
	private String orderNo;
	private String payFee;
	private String tradeNo;
	private String tradeDate;

}
