package com.weesharing.pay.service.impl.sync;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.weesharing.pay.common.CommonResult2;
import com.weesharing.pay.dto.pay.BankPay;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.entity.Refund;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.feign.BeanContext;
import com.weesharing.pay.feign.FastBankPayService;
import com.weesharing.pay.feign.param.BankConsumeData;
import com.weesharing.pay.feign.param.BankRefundData;
import com.weesharing.pay.feign.result.BankAuthResult;
import com.weesharing.pay.feign.result.BankConsumeResult;
import com.weesharing.pay.service.IPaySyncService;
import com.weesharing.pay.service.RedisService;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service(value = "bankPayService")
public class BankPayServiceImpl implements IPaySyncService {
	
	@Override
	public void doPay(Consume consume) {
		// 调用快捷支付
		RedisService redisService = BeanContext.getBean(RedisService.class);
		BankAuthResult auth = JSONUtil.toBean(redisService.get("bank_auth:" + consume.getOrderNo()), BankAuthResult.class);
		BankPay pay = JSONUtil.toBean(redisService.get("bank_pay:" + consume.getOrderNo()), BankPay.class);
		redisService.remove("bank_auth:" + consume.getOrderNo());
		redisService.remove("bank_pay:" + consume.getOrderNo());
		BankConsumeData tcd = new BankConsumeData(pay, auth, consume);
		CommonResult2<BankConsumeResult> commonResult = BeanContext.getBean(FastBankPayService.class).consume(tcd);
		log.info("请求快捷支付支付参数:{}, 结果: {}", JSONUtil.wrap(tcd, false), JSONUtil.wrap(commonResult, false));
		if (commonResult.getCode() == 200) {
			consume.setTradeNo(commonResult.getData().getTranFlow());
			consume.setTradeDate(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
			consume.setStatus(1);
			consume.insertOrUpdate();
		} else if(commonResult.getCode() != 200) {
			consume.setStatus(2);
			consume.insertOrUpdate();
			throw new ServiceException(commonResult.getMsg());
		}
		
	}
	
	
	@Override
	public void doRefund(Refund refund) {
		// 调用快捷支付
		BankRefundData trd = new BankRefundData(refund);
		CommonResult2<String> commonResult = BeanContext.getBean(FastBankPayService.class).refund(trd);
		log.info("请求快捷支付退款参数: {}, 结果: {}", JSONUtil.wrap(trd, false), JSONUtil.wrap(commonResult, false));
		if (commonResult.getCode() == 200) {
			refund.setRefundNo(commonResult.getData());
			refund.setTradeDate(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
			refund.setStatus(1);
			refund.insertOrUpdate();
		} else if (commonResult.getCode() != 200) {
			refund.setStatus(2);
			refund.insertOrUpdate();
			throw new ServiceException(commonResult.getMsg());
		}
	}

}
