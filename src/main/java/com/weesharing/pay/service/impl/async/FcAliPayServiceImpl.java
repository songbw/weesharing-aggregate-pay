package com.weesharing.pay.service.impl.async;

import org.springframework.stereotype.Service;

import com.weesharing.pay.common.CommonResult2;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.entity.Refund;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.feign.BeanContext;
import com.weesharing.pay.feign.WechatService;
import com.weesharing.pay.feign.param.AlipayConsumeData;
import com.weesharing.pay.service.IPayAsyncService;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("fcAliPayServiceImpl")
public class FcAliPayServiceImpl implements IPayAsyncService {

	@Override
	public String doPay(Consume consume) {
		AlipayConsumeData tcd = new AlipayConsumeData(consume);
		CommonResult2<String> commonResult = null;
		try {
			commonResult = BeanContext.getBean(WechatService.class).consume(tcd);
		}catch(Exception e) {
			throw new ServiceException(e.getMessage());
		}
		log.info("请求支付宝支付参数:{}, 结果: {}", JSONUtil.wrap(tcd, false), JSONUtil.wrap(commonResult, false));
		if (commonResult.getCode() == 200) {
			return JSONUtil.wrap(commonResult.getData(), false).toString();
		} else if(commonResult.getCode() != 200) {
			throw new ServiceException(commonResult.getMsg());
		}
		return null;
	}

	@Override
	public String doRefund(Refund refund) {
		throw new ServiceException("[异步退款]:不支持此退款方式");
	}

}
