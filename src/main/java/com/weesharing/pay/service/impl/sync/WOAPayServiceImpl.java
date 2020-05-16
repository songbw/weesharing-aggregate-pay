package com.weesharing.pay.service.impl.sync;

import com.weesharing.pay.utils.AggPayTradeDate;
import org.springframework.stereotype.Service;

import com.weesharing.pay.common.CommonResult;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.entity.Refund;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.feign.BeanContext;
import com.weesharing.pay.feign.WOAService;
import com.weesharing.pay.feign.param.WOAConsumeData;
import com.weesharing.pay.feign.param.WOARefundData;
import com.weesharing.pay.feign.result.ConsumeResult;
import com.weesharing.pay.feign.result.RefundResult;
import com.weesharing.pay.service.IPaySyncService;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("woaPayService")
public class WOAPayServiceImpl implements IPaySyncService{

	@Override
	public void doPay(Consume consume) {

		// 调用联机账户
		WOAConsumeData tcd = new WOAConsumeData(consume);
		CommonResult<ConsumeResult> commonResult = BeanContext.getBean(WOAService.class).consume(tcd);
		log.info("请求联机账户支付参数:{}, 结果: {}", JSONUtil.wrap(tcd, false), JSONUtil.wrap(commonResult, false));
		if (commonResult.getCode() == 200) {
			consume.setTradeNo(commonResult.getData().getTradeNo());
			consume.setTradeDate(AggPayTradeDate.buildTradeDate(commonResult.getData().getTradeDate()));
			consume.setStatus(1);
			consume.insertOrUpdate();
		} else if(commonResult.getCode() != 200) {
			consume.setStatus(2);
			consume.insertOrUpdate();
			throw new ServiceException(commonResult.getMessage());
		}

	}

	@Override
	public void doRefund(Refund refund) {
		// 调用联机账户
		WOARefundData  trd = new WOARefundData(refund);
		CommonResult<RefundResult> commonResult = BeanContext.getBean(WOAService.class).refund(trd);
		log.info("请求联机账户退款参数: {}, 结果: {}", JSONUtil.wrap(trd, false), JSONUtil.wrap(commonResult, false));
		if (commonResult.getCode() == 200) {
			refund.setRefundNo(commonResult.getData().getRefundNo());
			refund.setTradeDate(AggPayTradeDate.buildTradeDate(commonResult.getData().getTradeDate()));
			refund.setStatus(commonResult.getData().getStatus());
			refund.insertOrUpdate();
		} else if (commonResult.getCode() != 200) {
			refund.setStatus(2);
			refund.insertOrUpdate();
			throw new ServiceException(commonResult.getMessage());
		}
	}

}
