package com.weesharing.pay.service.handler;

import cn.hutool.json.JSONUtil;
import com.weesharing.pay.dto.paytype.PayType;
import com.weesharing.pay.utils.AggPayTradeDate;
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
		log.info("[支付回调] 入参:{}", JSONUtil.toJsonStr(notifyParam));
		Consume consume = getConsume(notifyParam.getOrderNo(), notifyParam.getPayType());
		log.info("[支付回调][支付号]:{}, [金额]:{}", notifyParam.getOrderNo(),  notifyParam.getPayFee());

		if(null ==consume.getRemark()){
			consume.setRemark(notifyParam.getPayType());
		}
		consume.setStatus(1);
		consume.setTradeNo(notifyParam.getTradeNo());
		consume.setTradeDate(AggPayTradeDate.buildTradeDate(notifyParam.getTradeDate()));
		consume.insertOrUpdate();
		//继续调用同步支付渠道
		payHandler.syncPay(notifyParam.getOrderNo(), true);
	}

	private Consume getConsume(String orderNo, String payType) {
		String rightPayType = PayType.getRightName(payType);
		if(null == rightPayType){
			log.info("[支付回调][获取支付详情]: 支付方式 为空");
			throw new ServiceException("参数payType错误");
		}
		QueryWrapper<Consume> consumeQuery = new QueryWrapper<Consume>();
		consumeQuery.eq("order_no", orderNo);
		consumeQuery.eq("pay_type", rightPayType);
		consumeQuery.eq("status",0);
		Consume consume = consumeService.getOne(consumeQuery);
		if(null == consume) {
			log.info("[支付回调][获取支付详情]: 订单已支付或支付失败, 订单号:{}, 支付方式:{}", orderNo, payType);
			throw new ServiceException("订单已支付或支付失败");
		}
		return consume;
	}

}
