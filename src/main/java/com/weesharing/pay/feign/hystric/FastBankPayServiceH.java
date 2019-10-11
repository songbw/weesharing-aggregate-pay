package com.weesharing.pay.feign.hystric;

import org.springframework.stereotype.Component;

import com.weesharing.pay.common.CommonResult;
import com.weesharing.pay.feign.FastBankPayService;
import com.weesharing.pay.feign.param.BankAuthBeanData;
import com.weesharing.pay.feign.result.BankAuthResult;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FastBankPayServiceH implements FastBankPayService{
	
	@Override
	public CommonResult<BankAuthResult> bankAuth(BankAuthBeanData data) {
		log.info("快捷支付鉴权失败, 参数:{}", JSONUtil.wrap(data, false) );
		return CommonResult.failed("快捷支付鉴权失败");
	}
	
}
