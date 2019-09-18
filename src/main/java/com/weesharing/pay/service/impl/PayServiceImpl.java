package com.weesharing.pay.service.impl;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.weesharing.pay.dto.ConsumeResultDTO;
import com.weesharing.pay.dto.PayDTO;
import com.weesharing.pay.dto.PrePayDTO;
import com.weesharing.pay.dto.PrePayResultDTO;
import com.weesharing.pay.dto.RefundDTO;
import com.weesharing.pay.dto.RefundResultDTO;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.service.IConsumeService;
import com.weesharing.pay.service.PayService;

import cn.hutool.core.date.DateUtil;

@Service
public class PayServiceImpl implements PayService{
	
	@Autowired
	private IConsumeService consumeService;

	@Override
	public PrePayResultDTO prePay(PrePayDTO prePay) {
		
		/**
		 * 1. 查询预支付记录是否存在
		 * 2. 如果存在:
		 * 		检查是否过期[30分钟]
		 * 			   未过期:  直接返回预支付号 
		 * 			   已过期:  生成新的预支付号
		 * 3.如果不存在: 
		 * 		生成新的预支付号
		 */
		Date now = new Date();
		QueryWrapper<Consume> consumeQuery = new QueryWrapper<Consume>();
		consumeQuery.eq("out_trade_no", prePay.getOutTradeNo());
		consumeQuery.eq("act_pay_fee", prePay.getActPayFee());
		consumeQuery.between("create_date", DateUtil.offsetMinute(now, -30) , now);
		Consume consume = consumeService.getOne(consumeQuery);
		if(consume != null) {
			return new PrePayResultDTO(consume.getOrderNo(), prePay.getOutTradeNo());
		}else {
			String orderNo = UUID.randomUUID().toString();
			consume  = prePay.convert();
			consume.setOrderNo(orderNo);
			consume.insert();
			return new PrePayResultDTO(orderNo, prePay.getOutTradeNo());
		}
	}

	@Override
	public String doPay(PayDTO pay) {
		Date now = new Date();
		QueryWrapper<Consume> consumeQuery = new QueryWrapper<Consume>();
		consumeQuery.eq("order_no", pay.getOrderNo());
		consumeQuery.eq("act_pay_fee", pay.getActPayFree());
		consumeQuery.between("create_date", DateUtil.offsetMinute(now, -30) , now);
		Consume consume = consumeService.getOne(consumeQuery);
		return null;
	}

	@Override
	public ConsumeResultDTO doQuery(String outTradeNo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String doRefund(RefundDTO refund) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RefundResultDTO doRefundQuery(String outTradeNo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void payNotifyHandler() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refundNotifyHandler() {
		// TODO Auto-generated method stub
		
	}

}
