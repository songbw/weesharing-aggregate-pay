package com.weesharing.pay.feign.hystric;

import org.springframework.stereotype.Component;

import com.weesharing.pay.common.CommonResult2;
import com.weesharing.pay.dto.BackRequest;
import com.weesharing.pay.feign.SSOService;
import com.weesharing.pay.feign.param.BalanceConsumeData;
import com.weesharing.pay.feign.param.BalanceRefundData;
import com.weesharing.pay.feign.result.BalanceDetail;
import com.weesharing.pay.feign.result.ConsumeResult;

@Component
public class SSOServiceH implements SSOService {

	@Override
	public CommonResult2<ConsumeResult> pinganPosBack(BackRequest bean) {
		return CommonResult2.failed("回调SSO失败");
	}
	
	@Override
	public CommonResult2<BalanceDetail> consume(BalanceConsumeData data) {
		return CommonResult2.failed("余额支付失败");
	}

	@Override
	public CommonResult2<BalanceDetail> refund(BalanceRefundData data) {
		return CommonResult2.failed("余额退款失败");
	}
	
}
