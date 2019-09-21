package com.weesharing.pay.dto;

import lombok.Data;

@Data
public class BackRequest {
    private BackBean data;
    private String sign;
    
	public BackRequest(BackBean data) {
		this.data = data;
		this.sign = "";
	}
}
