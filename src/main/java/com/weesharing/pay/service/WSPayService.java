package com.weesharing.pay.service;

import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.entity.Refund;

public interface WSPayService {

	public void doPay(Consume consume);	
	public void doRefund(Refund refund);
}
