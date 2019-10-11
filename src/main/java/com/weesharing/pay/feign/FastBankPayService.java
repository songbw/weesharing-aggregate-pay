package com.weesharing.pay.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.weesharing.pay.common.CommonResult;
import com.weesharing.pay.feign.hystric.FastBankPayServiceH;
import com.weesharing.pay.feign.param.BankAuthBeanData;
import com.weesharing.pay.feign.result.BankAuthResult;

@FeignClient(value = "cardPayment", fallback = FastBankPayServiceH.class)
public interface FastBankPayService {
	
    @RequestMapping(value = "/ztkx/cardPayment/auth", method = RequestMethod.POST)
    CommonResult<BankAuthResult> bankAuth(@RequestBody BankAuthBeanData data);
    
}
