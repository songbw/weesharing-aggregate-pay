package com.weesharing.pay.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.weesharing.pay.dto.AggregatePay;
import com.weesharing.pay.dto.AggregateRefund;
import com.weesharing.pay.dto.PrePay;
import com.weesharing.pay.dto.PrePayResult;
import com.weesharing.pay.dto.QueryConsumeResult;
import com.weesharing.pay.dto.QueryRefundResult;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.service.PayService;

@Service("balancePayService")
public class BalancePayServiceImpl implements PayService{
	
	@Override
	public PrePayResult prePay(PrePay prePay) {
		throw new ServiceException("调用错误");
	}

	@Override
	public String doPay(AggregatePay pay) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<QueryConsumeResult> doQuery(String outTradeNo) {
		throw new ServiceException("调用错误");
	}

	@Override
	public String doRefund(AggregateRefund refund) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<QueryRefundResult> doRefundQuery(String outTradeNo) {
		throw new ServiceException("调用错误");
	}

	@Override
	public void doRefund(Consume balanceConsume) {
		// TODO Auto-generated method stub
		
	}
}
