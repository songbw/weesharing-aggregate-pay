package com.weesharing.pay.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.weesharing.pay.common.CommonResult;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.entity.Refund;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.feign.WOCService;
import com.weesharing.pay.feign.param.WOCConsumeData;
import com.weesharing.pay.feign.param.WOCRefundData;
import com.weesharing.pay.feign.result.ConsumeResult;
import com.weesharing.pay.feign.result.RefundResult;
import com.weesharing.pay.service.IConsumeService;
import com.weesharing.pay.service.WSPayService;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service(value = "wocPayService")
public class WOCPayServiceImpl implements WSPayService {
	
	@Autowired
	private WOCService wocService;

	@Autowired
	private IConsumeService consumeService;
	
	@Override
	public void doPay(Consume consume) {
		// 调用惠民优选卡
		WOCConsumeData tcd = new WOCConsumeData(consume);
		CommonResult<ConsumeResult> commonResult = wocService.consume(tcd);
		log.debug("请求惠民优选卡支付参数:{}, 结果: {}", JSONUtil.wrap(tcd, false), JSONUtil.wrap(commonResult, false));
		if (commonResult.getCode() == 200) {
			consume.setTradeNo(commonResult.getData().getTradeNo());
			consume.setTradeDate(commonResult.getData().getTradeDate());
			consume.setStatus(1);
			consume.insertOrUpdate();
		} else if(commonResult.getCode() == 500) {
			consume.setStatus(2);
			consume.insertOrUpdate();
			throw new ServiceException(commonResult.getMessage());
		}
		
	}
	
	@Override
	public void doRefund(Refund refund) {
		
		QueryWrapper<Consume> consumeQuery = new QueryWrapper<Consume>();
		consumeQuery.eq("order_no", refund.getOrderNo());
		consumeQuery.eq("pay_type", refund.getPayType());
		consumeQuery.eq("card_no", refund.getCardNo());
		
		Consume consume = consumeService.getOne(consumeQuery);
		
		// 调用惠民优选卡
		WOCRefundData  trd = new WOCRefundData(consume, refund);
		CommonResult<RefundResult> commonResult = wocService.refund(trd);
		log.debug("请求惠民优选卡退款参数: {}, 结果: {}", JSONUtil.wrap(trd, false), JSONUtil.wrap(commonResult, false));
		if (commonResult.getCode() == 200) {
			refund.setRefundNo(commonResult.getData().getRefundNo());
			refund.setTradeDate(commonResult.getData().getTradeDate());
			refund.setStatus(1);
			refund.insertOrUpdate();
		} else if (commonResult.getCode() == 500) {
			refund.setStatus(2);
			refund.insertOrUpdate();
			throw new ServiceException(commonResult.getMessage());
		}
	}

}
