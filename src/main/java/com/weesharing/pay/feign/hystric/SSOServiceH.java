package com.weesharing.pay.feign.hystric;

import org.springframework.stereotype.Component;

import com.weesharing.pay.common.CommonResult;
import com.weesharing.pay.dto.BackRequest;
import com.weesharing.pay.feign.SSOService;
import com.weesharing.pay.feign.result.ConsumeResult;

@Component
public class SSOServiceH implements SSOService {

	@Override
	public CommonResult<ConsumeResult> pinganPosBack(BackRequest bean) {
		return CommonResult.failed("回调SSO失败");
	}
	
}
