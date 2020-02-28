package com.weesharing.pay.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.weesharing.pay.common.CommonResult2;
import com.weesharing.pay.feign.hystric.PingAnServiceH;
import com.weesharing.pay.feign.param.PingAnConsumeData;
import com.weesharing.pay.feign.param.PingAnRefundData;
import com.weesharing.pay.feign.param.YunChengRefundData;
import com.weesharing.pay.feign.result.PingAnResult;
import com.weesharing.pay.feign.result.YunChengRefundResult;

@FeignClient(value = "pingan-client", fallback = PingAnServiceH.class)
public interface PingAnService {

	@RequestMapping(value = "/pingan/payment/create", method = RequestMethod.POST)
	CommonResult2<PingAnResult> consume(@RequestBody PingAnConsumeData data);
	
	@RequestMapping(value = "/pingan/payment/refund", method = RequestMethod.POST)
	CommonResult2<PingAnResult> refund(@RequestBody PingAnRefundData data);
	
	@RequestMapping(value = "/wkyc/payment/refund", method = RequestMethod.POST)
	CommonResult2<YunChengRefundResult> yunChengRefund(@RequestBody YunChengRefundData data);

	
}
