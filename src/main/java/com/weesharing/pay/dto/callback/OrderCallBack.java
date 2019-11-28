package com.weesharing.pay.dto.callback;

import lombok.Data;

@Data
public class OrderCallBack {
    private OrderCallBackData data;
    private String sign;
    
	public OrderCallBack(OrderCallBackData data) {
		this.data = data;
		this.sign = "";
	}
}
