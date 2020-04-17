package com.weesharing.pay.feign;

import com.weesharing.pay.common.CommonResult2;
import com.weesharing.pay.feign.hystric.HuiYuPayServiceH;
import com.weesharing.pay.feign.param.HuiYuConsumeData;
import com.weesharing.pay.feign.param.HuiYuRefundData;
import com.weesharing.pay.feign.result.HuiYuPayResult;
import com.weesharing.pay.feign.result.HuiYuRefundResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value="commission", fallback = HuiYuPayServiceH.class)
public interface HuiYuPayService {

    @RequestMapping(value = "/fl/payment/personal/huiyu", method = RequestMethod.POST)
    CommonResult2<HuiYuPayResult> payment(@RequestBody HuiYuConsumeData data);

    @RequestMapping(value = "/fl/payment/personal/huiyu/refund", method = RequestMethod.POST)
    CommonResult2<HuiYuRefundResult> refund(@RequestBody HuiYuRefundData data);

}
