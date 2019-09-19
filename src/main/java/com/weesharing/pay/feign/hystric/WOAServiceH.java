package com.weesharing.pay.feign.hystric;

import org.springframework.stereotype.Component;

import com.weesharing.pay.common.CommonResult;
import com.weesharing.pay.feign.WOAService;
import com.weesharing.pay.feign.param.TradeConsumeData;
import com.weesharing.pay.feign.param.TradeRefundData;
import com.weesharing.pay.feign.result.ConsumeResult;
import com.weesharing.pay.feign.result.RefundResult;

@Component
public class WOAServiceH implements WOAService {

	@Override
	public CommonResult<ConsumeResult> consume(TradeConsumeData data) {
		return CommonResult.failed("无锡市民卡联机账户支付失败");
	}

	@Override
	public CommonResult<RefundResult> refund(TradeRefundData data) {
		return CommonResult.failed("无锡市民卡联机账户退款失败");
	}
	
}
