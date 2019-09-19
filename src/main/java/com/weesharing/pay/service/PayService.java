package com.weesharing.pay.service;

import java.util.List;

import com.weesharing.pay.dto.ConsumeResultDTO;
import com.weesharing.pay.dto.PayDTO;
import com.weesharing.pay.dto.PrePayDTO;
import com.weesharing.pay.dto.PrePayResultDTO;
import com.weesharing.pay.dto.RefundDTO;
import com.weesharing.pay.dto.RefundResultDTO;

public interface PayService {
	
	public PrePayResultDTO prePay(PrePayDTO prePay);
	
	public String doPay(PayDTO pay);
	
	public List<ConsumeResultDTO> doQuery(String outTradeNo);
	
	public String doRefund(RefundDTO refund);
	
	public List<RefundResultDTO> doRefundQuery(String outTradeNo);
	
	public void payNotifyHandler(String notifyUrl);
	
	public void refundNotifyHandler(String notifyUrl);

}
