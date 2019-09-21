package com.weesharing.pay.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.weesharing.pay.common.CommonResult;
import com.weesharing.pay.dto.BackRequest;
import com.weesharing.pay.feign.hystric.WOAServiceH;
import com.weesharing.pay.feign.result.ConsumeResult;

@FeignClient(value = "sso", fallback = WOAServiceH.class)
public interface SSOService {

    @RequestMapping(value = "/payment/pingan/back", method = RequestMethod.POST)
    CommonResult<ConsumeResult> pinganPosBack(@RequestBody BackRequest bean);

}
