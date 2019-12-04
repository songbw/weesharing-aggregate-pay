package com.weesharing.pay.service.impl.sync;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.weesharing.pay.common.CommonResult2;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.entity.Refund;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.feign.BeanContext;
import com.weesharing.pay.feign.SSOService;
import com.weesharing.pay.feign.param.BalanceConsumeData;
import com.weesharing.pay.feign.param.BalanceRefundData;
import com.weesharing.pay.feign.result.BalanceDetail;
import com.weesharing.pay.service.IPaySyncService;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("balancePayService")
public class BalancePayServiceImpl implements IPaySyncService{
	
	/**
	 * 调用余额账户
	 */
	@Override
	public void doPay(Consume consume) {
		BalanceConsumeData tcd = new BalanceConsumeData(consume);
		CommonResult2<BalanceDetail> commonResult = null;
		try {
			commonResult = BeanContext.getBean(SSOService.class).consume(tcd);
		}catch(Exception e) {
			e.printStackTrace();
		}
		log.info("请求余额支付参数:{}, 结果: {}", JSONUtil.wrap(tcd, false), JSONUtil.wrap(commonResult, false));
		if (commonResult.getCode() == 200) {
			consume.setPayer(commonResult.getData().getTelephone());
			consume.setTradeDate(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
			consume.setStatus(1);
			consume.insertOrUpdate();
		} else if(commonResult.getCode() != 200) {
			consume.setStatus(2);
			consume.insertOrUpdate();
			throw new ServiceException(commonResult.getMsg());
		}
		
	}
	
	/**
	 * 调用余额账户
	 */
	@Override
	public void doRefund(Refund refund) {
		BalanceRefundData  trd = new BalanceRefundData(refund);
		CommonResult2<BalanceDetail> commonResult = BeanContext.getBean(SSOService.class).refund(trd);
		log.info("请求余额退款参数: {}, 结果: {}", JSONUtil.wrap(trd, false), JSONUtil.wrap(commonResult, false));
		if (commonResult.getCode() == 200) {
			refund.setTradeDate(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
			refund.setTradeNo(commonResult.getData().getTelephone());
			refund.setStatus(1);
			refund.insertOrUpdate();
		} else if (commonResult.getCode() != 200) {
			refund.setStatus(2);
			refund.insertOrUpdate();
			throw new ServiceException(commonResult.getMsg());
		}
	}
	
	
}
