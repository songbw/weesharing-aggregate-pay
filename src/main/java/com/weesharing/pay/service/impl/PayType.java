package com.weesharing.pay.service.impl;


public enum PayType {
	
	BALANCE("balance"), CARD("card"), WOA("woa");
	
	private String name;
	
	private PayType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
