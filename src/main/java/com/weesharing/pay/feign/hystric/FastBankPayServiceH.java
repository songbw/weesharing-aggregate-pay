package com.weesharing.pay.feign.hystric;

import org.springframework.stereotype.Component;

import com.weesharing.pay.common.CommonResult2;
import com.weesharing.pay.feign.FastBankPayService;
import com.weesharing.pay.feign.param.BankAuthBeanData;
import com.weesharing.pay.feign.param.BankConsumeData;
import com.weesharing.pay.feign.param.BankRefundData;
import com.weesharing.pay.feign.result.BankAuthResult;
import com.weesharing.pay.feign.result.BankConsumeResult;

import cn.hutool.json.JSONUtil;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FastBankPayServiceH implements FallbackFactory<FastBankPayService> {

	@Override
	public FastBankPayService create(Throwable cause) {
		return new FastBankPayService() {

			@Override
			public CommonResult2<BankAuthResult> bankAuth(BankAuthBeanData data) {
				log.info("快捷支付鉴权失败, 参数:{}", JSONUtil.wrap(data, false));
				return CommonResult2.failed(cause.getMessage());
			}

			@Override
			public CommonResult2<BankConsumeResult> consume(BankConsumeData tcd) {
				log.info("快捷支付失败, 参数:{}", JSONUtil.wrap(tcd, false));
				return CommonResult2.failed(cause.getMessage());
			}

			@Override
			public CommonResult2<String> refund(BankRefundData trd) {
				log.info("快捷支付退款失败, 参数:{}", JSONUtil.wrap(trd, false));
				return CommonResult2.failed(cause.getMessage());
			}

			@Override
			public CommonResult2<String> refundStatus(String tranFlow) {
				log.info("快捷支付退款查询失败, 参数:{}", JSONUtil.wrap(tranFlow, false));
				return CommonResult2.failed(cause.getMessage());
			}

		};
	}

}
