package com.weesharing.pay.feign.hystric;

import org.springframework.stereotype.Component;

import com.weesharing.pay.common.CommonResult2;
import com.weesharing.pay.feign.WechatService;
import com.weesharing.pay.feign.param.XCXConsumeData;
import com.weesharing.pay.feign.param.XCXRefundData;
import com.weesharing.pay.feign.result.XCXPayResult;
import com.weesharing.pay.feign.result.XCXRefundResult;

@Component
public class WechatServiceH implements WechatService {

	@Override
	public CommonResult2<XCXPayResult> consume(XCXConsumeData data) {
		return CommonResult2.failed("微信小程序支付失败");
	}

	@Override
	public CommonResult2<XCXRefundResult> refund(XCXRefundData data) {
		return CommonResult2.failed("微信小程序退款失败");
	}

}
