package com.weesharing.pay.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.weesharing.pay.common.CommonResult;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.entity.Refund;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.feign.SSOService;
import com.weesharing.pay.feign.param.BalanceConsumeData;
import com.weesharing.pay.feign.param.BalanceRefundData;
import com.weesharing.pay.feign.result.ConsumeResult;
import com.weesharing.pay.feign.result.RefundResult;
import com.weesharing.pay.service.WSPayService;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("balancePayService")
public class BalancePayServiceImpl implements WSPayService{
	
	@Autowired
	private SSOService ssoService;
	
	/**
	 * 调用余额账户
	 */
	@Override
	public void doPay(Consume consume) {
		BalanceConsumeData tcd = new BalanceConsumeData(consume);
		CommonResult<ConsumeResult> commonResult = ssoService.consume(tcd);
		log.debug("请求余额支付参数:{}, 结果: {}", JSONUtil.wrap(tcd, false), JSONUtil.wrap(commonResult, false));
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
	
	/**
	 * 调用余额账户
	 */
	@Override
	public void doRefund(Refund refund) {
		BalanceRefundData  trd = new BalanceRefundData(refund);
		CommonResult<RefundResult> commonResult = ssoService.refund(trd);
		log.debug("请求余额退款参数: {}, 结果: {}", JSONUtil.wrap(trd, false), JSONUtil.wrap(commonResult, false));
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
