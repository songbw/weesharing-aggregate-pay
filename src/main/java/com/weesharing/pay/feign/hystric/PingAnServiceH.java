package com.weesharing.pay.feign.hystric;

import org.springframework.stereotype.Component;

import com.weesharing.pay.common.CommonResult2;
import com.weesharing.pay.feign.PingAnService;
import com.weesharing.pay.feign.param.PingAnConsumeData;
import com.weesharing.pay.feign.param.PingAnRefundData;
import com.weesharing.pay.feign.result.PingAnResult;

@Component
public class PingAnServiceH implements PingAnService {

	@Override
	public CommonResult2<PingAnResult> consume(PingAnConsumeData data) {
		return CommonResult2.failed("平安预支付失败");
	}

	@Override
	public CommonResult2<PingAnResult> refund(PingAnRefundData data) {
		return CommonResult2.failed("平安退款失败");
	}
	
}
