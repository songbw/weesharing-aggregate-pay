package com.weesharing.pay.dto.notify;

import lombok.Data;

@Data
public class PayNotify<T> {
	
	private String serviceName;
	private String platformNo;
	private T respData;
	private String sign;
	
}
