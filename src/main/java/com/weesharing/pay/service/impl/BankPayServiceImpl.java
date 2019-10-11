package com.weesharing.pay.service.impl;

import org.springframework.stereotype.Service;

import com.weesharing.pay.common.CommonResult;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.entity.Refund;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.feign.BeanContext;
import com.weesharing.pay.feign.FastBankPayService;
import com.weesharing.pay.feign.param.BankConsumeData;
import com.weesharing.pay.feign.param.BankRefundData;
import com.weesharing.pay.service.IPayService;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service(value = "bankPayService")
public class BankPayServiceImpl implements IPayService {
	
	@Override
	public void doPay(Consume consume) {
		// 调用快捷支付
		BankConsumeData tcd = new BankConsumeData(consume);
		CommonResult<?> commonResult = BeanContext.getBean(FastBankPayService.class).consume(tcd);
		log.info("请求快捷支付支付参数:{}, 结果: {}", JSONUtil.wrap(tcd, false), JSONUtil.wrap(commonResult, false));
		if (commonResult.getCode() == 200) {
			consume.setTradeNo("");
			consume.setTradeDate("");
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
		// 调用快捷支付
		BankRefundData trd = new BankRefundData(refund);
		CommonResult<?> commonResult = BeanContext.getBean(FastBankPayService.class).refund(trd);
		log.info("请求快捷支付退款参数: {}, 结果: {}", JSONUtil.wrap(trd, false), JSONUtil.wrap(commonResult, false));
		if (commonResult.getCode() == 200) {
			refund.setRefundNo("");
			refund.setTradeDate("");
			refund.setStatus(1);
			refund.insertOrUpdate();
		} else if (commonResult.getCode() == 500) {
			refund.setStatus(2);
			refund.insertOrUpdate();
			throw new ServiceException(commonResult.getMessage());
		}
	}

}
