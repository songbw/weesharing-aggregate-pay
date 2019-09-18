package com.weesharing.pay.service;

import com.weesharing.pay.dto.ConsumeResultDTO;
import com.weesharing.pay.dto.PayDTO;
import com.weesharing.pay.dto.PrePayDTO;
import com.weesharing.pay.dto.PrePayResultDTO;
import com.weesharing.pay.dto.RefundDTO;
import com.weesharing.pay.dto.RefundResultDTO;

public interface PayService {
	
	public PrePayResultDTO prePay(PrePayDTO prePay);
	
	public String doPay(PayDTO pay);
	
	public ConsumeResultDTO doQuery(String outTradeNo);
	
	public String doRefund(RefundDTO refund);
	
	public RefundResultDTO doRefundQuery(String outTradeNo);
	
	public void payNotifyHandler();
	
	public void refundNotifyHandler();

}
