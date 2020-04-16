package com.weesharing.pay.feign.hystric;

import com.weesharing.pay.common.CommonResult2;
import com.weesharing.pay.feign.HuiYuPayService;
import com.weesharing.pay.feign.param.HuiYuConsumeData;
import com.weesharing.pay.feign.param.HuiYuRefundData;
import com.weesharing.pay.feign.result.HuiYuPayResult;
import com.weesharing.pay.feign.result.HuiYuRefundResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Component
public class HuiYuPayServiceH implements HuiYuPayService {

    @Override
    public CommonResult2<HuiYuPayResult>
    payment(@RequestBody HuiYuConsumeData data){
        log.error("惠余支付失败");
        return CommonResult2.failed("惠余支付失败");
    }

    @Override
    public CommonResult2<HuiYuRefundResult> refund(@RequestBody HuiYuRefundData data){
        log.error("惠余退款失败");
        return CommonResult2.failed("惠余退款失败");
    }
}
