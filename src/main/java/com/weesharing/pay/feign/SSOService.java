package com.weesharing.pay.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.weesharing.pay.common.CommonResult2;
import com.weesharing.pay.dto.callback.OrderCallBack;
import com.weesharing.pay.feign.hystric.SSOServiceH;
import com.weesharing.pay.feign.param.BalanceConsumeData;
import com.weesharing.pay.feign.param.BalanceRefundData;
import com.weesharing.pay.feign.result.BalanceDetail;
import com.weesharing.pay.feign.result.ConsumeResult;

@FeignClient(value = "sso", fallback = SSOServiceH.class)
public interface SSOService {

	@RequestMapping(value = "/balance/consume", method = RequestMethod.PUT)
	CommonResult2<?> consume(@RequestBody BalanceConsumeData data);

	@RequestMapping(value = "/balance/refund", method = RequestMethod.PUT)
	CommonResult2<BalanceDetail> refund(@RequestBody BalanceRefundData data);

	@RequestMapping(value = "/payment/pingan/back", method = RequestMethod.POST)
	CommonResult2<ConsumeResult> pinganPosBack(@RequestBody OrderCallBack bean);

}
