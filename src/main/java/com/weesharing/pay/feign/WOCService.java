package com.weesharing.pay.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.weesharing.pay.common.CommonResult;
import com.weesharing.pay.feign.hystric.WOCServiceH;
import com.weesharing.pay.feign.param.WOCConsumeData;
import com.weesharing.pay.feign.param.WOCRefundData;
import com.weesharing.pay.feign.result.PaymentResult;
import com.weesharing.pay.feign.result.WOCRefundResult;

@FeignClient(value = "woc", fallback = WOCServiceH.class)
public interface WOCService {

    @RequestMapping(value = "/woc/cardconsume/pay", method = RequestMethod.POST)
    CommonResult<PaymentResult> consume(@RequestBody List<WOCConsumeData> data);

    @RequestMapping(value = "/woc/cardrefund/refund", method = RequestMethod.POST)
    CommonResult<WOCRefundResult> refund(@RequestBody WOCRefundData data);
}
