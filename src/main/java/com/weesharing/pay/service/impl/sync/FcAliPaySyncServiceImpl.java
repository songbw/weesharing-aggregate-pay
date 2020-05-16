package com.weesharing.pay.service.impl.sync;

import java.util.Date;

import com.weesharing.pay.utils.AggPayTradeDate;
import org.springframework.stereotype.Service;

import com.weesharing.pay.common.CommonResult2;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.entity.Refund;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.feign.BeanContext;
import com.weesharing.pay.feign.WechatService;
import com.weesharing.pay.feign.param.AlipayRefundData;
import com.weesharing.pay.feign.result.AlipayRefundResult;
import com.weesharing.pay.service.IPaySyncService;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("fcAliPaySyncServiceImpl")
public class FcAliPaySyncServiceImpl implements IPaySyncService {

	@Override
	public void doPay(Consume consume) {
		throw new ServiceException("[同步支持]:不支持此支付方式");

	}

	@Override
	public void doRefund(Refund refund) {
		AlipayRefundData  trd = new AlipayRefundData(refund);
		CommonResult2<AlipayRefundResult> commonResult = BeanContext.getBean(WechatService.class).refund(trd);
		log.info("请求支付宝退款参数: {}, 结果: {}", JSONUtil.wrap(trd, false), JSONUtil.wrap(commonResult, false));
		if (commonResult.getCode() == 200) {
			refund.setTradeDate(AggPayTradeDate.buildTradeDate());
			refund.setTradeNo(commonResult.getData().getRefundNo());
			refund.setStatus(1);
			refund.insertOrUpdate();
		} else if (commonResult.getCode() != 200) {
			refund.setStatus(2);
			refund.insertOrUpdate();
			throw new ServiceException(commonResult.getMsg());
		}

	}

}
