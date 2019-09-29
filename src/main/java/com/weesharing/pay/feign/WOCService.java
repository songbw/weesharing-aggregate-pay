package com.weesharing.pay.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.weesharing.pay.common.CommonResult;
import com.weesharing.pay.feign.hystric.WOCServiceH;
import com.weesharing.pay.feign.param.WOCConsumeData;
import com.weesharing.pay.feign.param.WOCRefundData;
import com.weesharing.pay.feign.result.ConsumeResult;
import com.weesharing.pay.feign.result.RefundResult;

@FeignClient(value = "woc", fallback = WOCServiceH.class)
public interface WOCService {

    @RequestMapping(value = "/cardconsume/pay", method = RequestMethod.POST)
    CommonResult<ConsumeResult> consume(@RequestBody WOCConsumeData data);

    @RequestMapping(value = "/cardrefund/refund", method = RequestMethod.POST)
    CommonResult<RefundResult> refund(@RequestBody WOCRefundData data);
}
