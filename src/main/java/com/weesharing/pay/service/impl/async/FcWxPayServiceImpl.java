package com.weesharing.pay.service.impl.async;

import org.springframework.stereotype.Service;

import com.weesharing.pay.common.CommonResult2;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.entity.Refund;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.feign.BeanContext;
import com.weesharing.pay.feign.WechatService;
import com.weesharing.pay.feign.param.JSApiConsumeData;
import com.weesharing.pay.feign.param.JSApiRefundData;
import com.weesharing.pay.feign.result.JSApiPayResult;
import com.weesharing.pay.feign.result.JSApiRefundResult;
import com.weesharing.pay.service.IPayAsyncService;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("fcWxPayService")
public class FcWxPayServiceImpl implements IPayAsyncService {

	@Override
	public String doPay(Consume consume) {
		JSApiConsumeData tcd = new JSApiConsumeData(consume);
		CommonResult2<JSApiPayResult> commonResult = null;
		try {
			commonResult = BeanContext.getBean(WechatService.class).consume(tcd);
		}catch(Exception e) {
			throw new ServiceException(e.getMessage());
		}
		log.info("请求微信公众号支付参数:{}, 结果: {}", JSONUtil.wrap(tcd, false), JSONUtil.wrap(commonResult, false));
		if (commonResult.getCode() == 200) {
			return JSONUtil.wrap(commonResult.getData(), false).toString();
		} else if(commonResult.getCode() != 200) {
			throw new ServiceException(commonResult.getMsg());
		}
		return null;
	}

	@Override
	public String doRefund(Refund refund) {
		JSApiRefundData trd = new JSApiRefundData(refund);
		CommonResult2<JSApiRefundResult> commonResult = BeanContext.getBean(WechatService.class).refund(trd);
		log.info("请求微信公众号支付退款参数: {}, 结果: {}", JSONUtil.wrap(trd, false), JSONUtil.wrap(commonResult, false));
		if (commonResult.getCode() == 200) {
			refund.setRefundNo(commonResult.getData().getWechatRefundNo());
			refund.insertOrUpdate();
		} else if (commonResult.getCode() != 200) {
			refund.setStatus(2);
			refund.insertOrUpdate();
			throw new ServiceException(commonResult.getMsg());
		}
		return null;
	}

}
