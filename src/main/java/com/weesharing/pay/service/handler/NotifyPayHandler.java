package com.weesharing.pay.service.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.weesharing.pay.dto.notify.CommonPayNotify;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.service.IConsumeService;

import lombok.extern.slf4j.Slf4j;

/**
 * 处理支付异步回调函数
 * @author zp
 *
 */
@Slf4j
@Service
public class NotifyPayHandler {
	
	@Autowired
	private PayHandler payHandler;
	
	@Autowired
	private IConsumeService consumeService;
	
	public void PayNotifyService(CommonPayNotify notifyParam) {
		Consume consume = getConsume(notifyParam.getOrderNo(), notifyParam.getPayType());
		log.info("[支付回调][支付号]:{}, [金额]:{}", notifyParam.getOrderNo(),  notifyParam.getPayFee());
		consume.setStatus(1);
		consume.setTradeNo(notifyParam.getTradeNo());
		consume.setTradeDate(notifyParam.getTradeDate());
		consume.insertOrUpdate();
		//继续调用同步支付渠道
		payHandler.syncPay(notifyParam.getOrderNo(), false);
	}
	
	private Consume getConsume(String orderNo, String payType) {
		QueryWrapper<Consume> consumeQuery = new QueryWrapper<Consume>();
		consumeQuery.eq("order_no", orderNo);
		consumeQuery.eq("pay_type", payType);
		Consume consume = consumeService.getOne(consumeQuery);
		if(consume.getStatus() != 0) {
			log.info("[支付回调][获取支付详情]: 订单已支付或支付失败, 订单号:{}, 支付方式:{}", orderNo, payType);
			throw new ServiceException("订单已支付或支付失败");
		}
		return consume;
	}

}
