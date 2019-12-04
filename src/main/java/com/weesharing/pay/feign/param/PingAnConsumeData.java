package com.weesharing.pay.feign.param;

import com.weesharing.pay.entity.Consume;

import lombok.Data;

@Data
public class PingAnConsumeData {

    private String goodsName;
    private String mchOrderNo;
    private Integer amount;
    private String memberNo ;
    
    public PingAnConsumeData() {
	}
    
	public PingAnConsumeData(Consume consume) {
		this.memberNo = consume.getCardNo();
		this.mchOrderNo = consume.getOrderNo();
		this.amount = Integer.parseInt(consume.getActPayFee());
		this.goodsName = "下单支付";
	}

}