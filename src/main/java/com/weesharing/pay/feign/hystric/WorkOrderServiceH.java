package com.weesharing.pay.feign.hystric;

import org.springframework.stereotype.Component;

import com.weesharing.pay.dto.QueryRefundResult;
import com.weesharing.pay.feign.WorkOrderService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WorkOrderServiceH implements WorkOrderService{

	@Override
	public void refundNotify(QueryRefundResult data) {
		log.info("回调退款失败");
		
	}
}
