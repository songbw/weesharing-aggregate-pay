package com.weesharing.pay.feign.param;

import com.weesharing.pay.entity.Consume;

import lombok.Data;

@Data
public class BalanceConsumeData {

    private String openId;
    private String orderNo;
    private Integer saleAmount;
    
    public BalanceConsumeData() {
	}
    
	public BalanceConsumeData(Consume consume) {
		this.openId = consume.getCardNo();
		this.orderNo = consume.getOrderNo();
		this.saleAmount = Integer.parseInt(consume.getActPayFee());
	}

}