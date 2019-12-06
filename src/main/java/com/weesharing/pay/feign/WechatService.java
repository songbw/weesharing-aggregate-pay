package com.weesharing.pay.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.weesharing.pay.common.CommonResult2;
import com.weesharing.pay.feign.hystric.WechatServiceH;
import com.weesharing.pay.feign.param.XCXConsumeData;
import com.weesharing.pay.feign.param.XCXRefundData;
import com.weesharing.pay.feign.result.XCXPayResult;
import com.weesharing.pay.feign.result.XCXRefundResult;

@FeignClient(value = "miniApp", fallback = WechatServiceH.class)
public interface WechatService {

    @RequestMapping(value = "/wechat/unifiedOrder/mini", method = RequestMethod.POST)
    CommonResult2<XCXPayResult> consume(@RequestBody XCXConsumeData data);

    @RequestMapping(value = "/wechat/refund/mini", method = RequestMethod.POST)
    CommonResult2<XCXRefundResult> refund(@RequestBody XCXRefundData data);
}
