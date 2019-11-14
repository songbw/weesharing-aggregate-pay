package com.weesharing.pay.service;

import java.util.List;
import java.util.Map;

import com.weesharing.pay.common.CommonPage;
import com.weesharing.pay.dto.AggregatePay;
import com.weesharing.pay.dto.AggregateRefund;
import com.weesharing.pay.dto.BankAuthBean;
import com.weesharing.pay.dto.PrePay;
import com.weesharing.pay.dto.PrePayResult;
import com.weesharing.pay.dto.QueryConsumeRefundRequest;
import com.weesharing.pay.dto.QueryConsumeRefundResult;
import com.weesharing.pay.dto.QueryConsumeResult;
import com.weesharing.pay.dto.QueryRefundResult;

public interface AggregatePayService {
	
	public PrePayResult prePay(PrePay prePay);
	
	public String doPay(AggregatePay pay);
	
	public List<QueryConsumeResult> doQuery(String orderNo);
	
	public Integer doPreQuery(String orderNo);
	
	public Map<String, List<QueryConsumeResult>> doBatchQueryPay(String orderNo);
	
	public String doRefund(AggregateRefund refund);
	
	public List<QueryRefundResult> doRefundQuery(String outRefundNo);
	
	public Map<String, List<QueryRefundResult>> doBatchQueryRefund(String outRefundNos);
	
	public String fastPayAuth(BankAuthBean auth);
	
	public CommonPage<QueryConsumeRefundResult> doQueryConsumeRefund(QueryConsumeRefundRequest request);

}
