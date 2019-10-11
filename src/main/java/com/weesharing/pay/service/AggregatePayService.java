package com.weesharing.pay.service;

import java.util.List;

import com.weesharing.pay.dto.AggregatePay;
import com.weesharing.pay.dto.AggregateRefund;
import com.weesharing.pay.dto.BankAuthBean;
import com.weesharing.pay.dto.PrePay;
import com.weesharing.pay.dto.PrePayResult;
import com.weesharing.pay.dto.QueryConsumeResult;
import com.weesharing.pay.dto.QueryRefundResult;

public interface AggregatePayService {
	
	public PrePayResult prePay(PrePay prePay);
	
	public String doPay(AggregatePay pay);
	
	public List<QueryConsumeResult> doQuery(String outTradeNo);
	
	public String doRefund(AggregateRefund refund);
	
	public List<QueryRefundResult> doRefundQuery(String outTradeNo);
	
	public String fastPayAuth(BankAuthBean auth);

}
