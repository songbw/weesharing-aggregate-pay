package com.weesharing.pay.service.impl.sync;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.weesharing.pay.common.CommonResult;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.entity.Refund;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.feign.BeanContext;
import com.weesharing.pay.feign.WOCService;
import com.weesharing.pay.feign.param.WOCConsumeData;
import com.weesharing.pay.feign.param.WOCRefundData;
import com.weesharing.pay.feign.result.PaymentResult;
import com.weesharing.pay.feign.result.WOCRefundResult;
import com.weesharing.pay.service.IConsumeService;
import com.weesharing.pay.service.IPaySyncService;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service(value = "wocPayService")
public class WOCPayServiceImpl implements IPaySyncService {
	
	@Override
	public void doPay(Consume consume) {
		// 调用惠民优选卡
		List<WOCConsumeData> tcds = new ArrayList<WOCConsumeData>();
		tcds.add(new WOCConsumeData(consume));
		CommonResult<PaymentResult> commonResult = BeanContext.getBean(WOCService.class).consume(tcds);
		log.info("请求惠民优选卡支付参数:{}, 结果: {}", JSONUtil.wrap(tcds, false), JSONUtil.wrap(commonResult, false));
		if (commonResult.getCode() == 200) {
			consume.setTradeNo(commonResult.getData().getCardPayResponseBeanList().get(0).getPaymenttransnum());
			consume.setTradeDate(commonResult.getData().getCardPayResponseBeanList().get(0).getTranstime());
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
		
		QueryWrapper<Consume> consumeQuery = new QueryWrapper<Consume>();
		consumeQuery.eq("order_no", refund.getOrderNo());
		consumeQuery.eq("pay_type", refund.getPayType());
		consumeQuery.eq("card_no", refund.getCardNo());
		Consume consume = BeanContext.getBean(IConsumeService.class).getOne(consumeQuery);
		
		// 调用惠民优选卡
		WOCRefundData trd = new WOCRefundData(consume, refund);
		CommonResult<WOCRefundResult> commonResult = BeanContext.getBean(WOCService.class).refund(trd);
		log.info("请求惠民优选卡退款参数: {}, 结果: {}", JSONUtil.wrap(trd, false), JSONUtil.wrap(commonResult, false));
		if (commonResult.getCode() == 200) {
			refund.setRefundNo(commonResult.getData().getRefundtransnum());
			refund.setTradeDate(commonResult.getData().getTranstime());
			refund.setStatus(1);
			refund.insertOrUpdate();
		} else if (commonResult.getCode() != 200) {
			refund.setStatus(2);
			refund.insertOrUpdate();
			throw new ServiceException(commonResult.getMessage());
		}
		
	}

}
