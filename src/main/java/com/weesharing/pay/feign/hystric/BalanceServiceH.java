package com.weesharing.pay.feign.hystric;

import org.springframework.stereotype.Component;

import com.weesharing.pay.common.CommonResult;
import com.weesharing.pay.feign.BalanceService;
import com.weesharing.pay.feign.WOAService;
import com.weesharing.pay.feign.param.WOAConsumeData;
import com.weesharing.pay.feign.param.WOARefundData;
import com.weesharing.pay.feign.result.ConsumeResult;
import com.weesharing.pay.feign.result.RefundResult;

@Component
public class BalanceServiceH implements BalanceService {

	@Override
	public CommonResult<ConsumeResult> consume(WOAConsumeData data) {
		return CommonResult.failed("余额支付失败");
	}

	@Override
	public CommonResult<RefundResult> refund(WOARefundData data) {
		return CommonResult.failed("余额退款失败");
	}
	
}
