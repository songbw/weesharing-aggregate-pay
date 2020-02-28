package com.weesharing.pay.service.impl.async;

import org.springframework.stereotype.Service;

import com.weesharing.pay.common.CommonResult2;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.entity.Refund;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.feign.BeanContext;
import com.weesharing.pay.feign.PingAnService;
import com.weesharing.pay.feign.param.YunChengConsumeData;
import com.weesharing.pay.feign.param.YunChengRefundData;
import com.weesharing.pay.feign.result.YunChengRefundResult;
import com.weesharing.pay.service.IPayAsyncService;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("yunChengPayServiceImpl")
public class YunChengPayServiceImpl implements IPayAsyncService {

	@Override
	public String doPay(Consume consume) {
		YunChengConsumeData tcd = new YunChengConsumeData(consume);
		log.info("请求云城支付参数:{}", JSONUtil.wrap(tcd, false));
		return JSONUtil.wrap(tcd, false).toString();
	}

	@Override
	public String doRefund(Refund refund) {
		YunChengRefundData trd = new YunChengRefundData(refund);
		CommonResult2<YunChengRefundResult> commonResult = BeanContext.getBean(PingAnService.class).yunChengRefund(trd);
		log.info("请求云城支付退款参数: {}, 结果: {}", JSONUtil.wrap(trd, false), JSONUtil.wrap(commonResult, false));
		if (commonResult.getCode() == 200) {
			refund.setRefundNo(commonResult.getData().getRefundNo());
			refund.insertOrUpdate();
		} else if (commonResult.getCode() != 200) {
			refund.setStatus(2);
			refund.insertOrUpdate();
			throw new ServiceException(commonResult.getMsg());
		}
		return null;
	}

}
