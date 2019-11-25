package com.weesharing.pay.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.weesharing.pay.common.CommonResult2;
import com.weesharing.pay.feign.hystric.FastBankPayServiceFallBack;
import com.weesharing.pay.feign.param.BankAuthBeanData;
import com.weesharing.pay.feign.param.BankConsumeData;
import com.weesharing.pay.feign.param.BankRefundData;
import com.weesharing.pay.feign.result.BankAuthResult;
import com.weesharing.pay.feign.result.BankConsumeResult;

@FeignClient(value = "cardPayment", fallbackFactory = FastBankPayServiceFallBack.class)
public interface FastBankPayService {
	
    @RequestMapping(value = "/ztkx/cardPayment/auth", method = RequestMethod.POST)
    CommonResult2<BankAuthResult> bankAuth(@RequestBody BankAuthBeanData data);

    @RequestMapping(value = "/ztkx/cardPayment/payment", method = RequestMethod.POST)
	CommonResult2<BankConsumeResult> consume(BankConsumeData tcd);

    @RequestMapping(value = "/ztkx/refund", method = RequestMethod.POST)
	CommonResult2<String> refund(BankRefundData trd);
    
    @RequestMapping(value = "/ztkx/refund/status", method = RequestMethod.GET)
	CommonResult2<String> refundStatus(@RequestParam("tranFlow") String tranFlow);
    
}
