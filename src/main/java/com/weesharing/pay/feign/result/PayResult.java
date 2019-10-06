package com.weesharing.pay.feign.result;

import lombok.Data;

@Data
public class PayResult {
	
	private String paymenttransnum;
	private String balance;
	private String transtime;
	private String cardnum;
	
}