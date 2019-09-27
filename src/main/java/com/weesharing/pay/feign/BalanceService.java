package com.weesharing.pay.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.weesharing.pay.common.CommonResult;
import com.weesharing.pay.feign.hystric.BalanceServiceH;
import com.weesharing.pay.feign.param.WOAConsumeData;
import com.weesharing.pay.feign.param.WOARefundData;
import com.weesharing.pay.feign.result.ConsumeResult;
import com.weesharing.pay.feign.result.RefundResult;

//@FeignClient(value = "woa", fallback = BalanceServiceH.class)
public interface BalanceService {

    @RequestMapping(value = "/wxpos/consume", method = RequestMethod.POST)
    CommonResult<ConsumeResult> consume(@RequestBody WOAConsumeData data);

    @RequestMapping(value = "/wxpos/refund", method = RequestMethod.POST)
    CommonResult<RefundResult> refund(@RequestBody WOARefundData data);
}
