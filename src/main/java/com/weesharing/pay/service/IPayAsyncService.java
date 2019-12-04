package com.weesharing.pay.service;

import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.entity.Refund;

public interface IPayAsyncService {

	public String doPay(Consume consume);	
	
	public String doRefund(Refund refund);
}
