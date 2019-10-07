package com.weesharing.pay.feign.param;

import java.math.BigDecimal;

import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.entity.Refund;

import lombok.Data;

@Data
public class WOCRefundData {
	
	private String  cardnum;
	private String  phonenum;
	private String  ordernum;
	private String  paymenttransnum;
	private String  paymentamount;
	private String  refundamount;
	private String  ordertime;
	
	public WOCRefundData(Consume consume, Refund refund) {
		this.cardnum = refund.getCardNo();
		this.phonenum = consume.getPayer();
		this.ordernum = refund.getOrderNo();
		this.paymentamount = new BigDecimal(consume.getActPayFee()).divide(BigDecimal.valueOf(100)).toString();
		this.paymenttransnum = consume.getTradeNo();
		this.refundamount = new BigDecimal(refund.getRefundFee()).divide(BigDecimal.valueOf(100)).toString();
		this.ordertime = consume.getTradeDate();
	}
}
