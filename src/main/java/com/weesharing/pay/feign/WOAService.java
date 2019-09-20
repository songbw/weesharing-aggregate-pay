package com.weesharing.pay.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.weesharing.pay.common.CommonResult;
import com.weesharing.pay.feign.hystric.WOAServiceH;
import com.weesharing.pay.feign.param.TradeConsumeData;
import com.weesharing.pay.feign.param.TradeRefundData;
import com.weesharing.pay.feign.result.ConsumeResult;
import com.weesharing.pay.feign.result.RefundResult;

//@FeignClient(value = "woa", url = "${feign.client.woa}", fallback = WOAServiceH.class)
@FeignClient(value = "woa", fallback = WOAServiceH.class)
public interface WOAService {

    @RequestMapping(value = "/wxpos/consume", method = RequestMethod.POST)
    CommonResult<ConsumeResult> consume(@RequestBody TradeConsumeData data);

    @RequestMapping(value = "/wxpos/refund", method = RequestMethod.POST)
    CommonResult<RefundResult> refund(@RequestBody TradeRefundData data);
}
