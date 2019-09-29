package com.weesharing.pay.feign.hystric;

import org.springframework.stereotype.Component;

import com.weesharing.pay.common.CommonResult;
import com.weesharing.pay.feign.WOCService;
import com.weesharing.pay.feign.param.WOCConsumeData;
import com.weesharing.pay.feign.param.WOCRefundData;
import com.weesharing.pay.feign.result.ConsumeResult;
import com.weesharing.pay.feign.result.RefundResult;

@Component
public class WOCServiceH implements WOCService{

	@Override
	public CommonResult<ConsumeResult> consume(WOCConsumeData data) {
		return CommonResult.failed("无锡惠民优选卡支付失败");
	}

	@Override
	public CommonResult<RefundResult> refund(WOCRefundData data) {
		return CommonResult.failed("无锡惠民优选卡退款失败");
	}

}
