package com.weesharing.pay.feign.hystric;

import com.weesharing.pay.feign.param.*;
import org.springframework.stereotype.Component;

import com.weesharing.pay.common.CommonResult2;
import com.weesharing.pay.feign.WechatService;
import com.weesharing.pay.feign.result.AlipayRefundResult;
import com.weesharing.pay.feign.result.JSApiPayResult;
import com.weesharing.pay.feign.result.JSApiRefundResult;
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

	@Override
	public CommonResult2<JSApiPayResult> consume(JSApiConsumeData data) {
		return CommonResult2.failed("微信公众号支付失败");
	}

	@Override
	public CommonResult2<JSApiRefundResult> refund(JSApiRefundData data) {
		return CommonResult2.failed("微信公众号退款失败");
	}

	@Override
	public CommonResult2<String> consume(AlipayConsumeData data) {
		return CommonResult2.failed("支付宝支付失败");
	}

	@Override
	public CommonResult2<String> consume(AliPayJsSdkConsumeData data) {
		return CommonResult2.failed("支付宝JsSdk支付失败");
	}

	@Override
	public CommonResult2<AlipayRefundResult> refund(AlipayRefundData data) {
		return CommonResult2.failed("支付宝退款失败");
	}

}
