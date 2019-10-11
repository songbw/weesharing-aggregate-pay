package com.weesharing.pay.feign.result;

import lombok.Data;

@Data
public class BankAuthResult {
	
	private int id;
	private String custId;
	private String merOrderId;
	private String phoneToken;

}
