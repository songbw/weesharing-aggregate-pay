package com.weesharing.pay.feign.hystric;

import org.springframework.stereotype.Component;

import com.weesharing.pay.common.CommonResult;
import com.weesharing.pay.dto.BackRequest;
import com.weesharing.pay.feign.SSOService;
import com.weesharing.pay.feign.param.BalanceConsumeData;
import com.weesharing.pay.feign.param.BalanceRefundData;
import com.weesharing.pay.feign.result.ConsumeResult;
import com.weesharing.pay.feign.result.RefundResult;

@Component
public class SSOServiceH implements SSOService {

	@Override
	public CommonResult<ConsumeResult> pinganPosBack(BackRequest bean) {
		return CommonResult.failed("回调SSO失败");
	}
	
	@Override
	public CommonResult<ConsumeResult> consume(BalanceConsumeData data) {
		return CommonResult.failed("余额支付失败");
	}

	@Override
	public CommonResult<RefundResult> refund(BalanceRefundData data) {
		return CommonResult.failed("余额退款失败");
	}
	
}
