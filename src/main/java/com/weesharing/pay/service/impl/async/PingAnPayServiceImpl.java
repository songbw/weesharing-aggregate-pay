package com.weesharing.pay.service.impl.async;

import org.springframework.stereotype.Service;

import com.weesharing.pay.common.CommonResult2;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.entity.Refund;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.feign.BeanContext;
import com.weesharing.pay.feign.PingAnService;
import com.weesharing.pay.feign.param.PingAnConsumeData;
import com.weesharing.pay.feign.result.PingAnResult;
import com.weesharing.pay.service.IPayAsyncService;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("pingAnPayService")
public class PingAnPayServiceImpl implements IPayAsyncService {

	@Override
	public String doPay(Consume consume) {
		PingAnConsumeData tcd = new PingAnConsumeData(consume);
		CommonResult2<PingAnResult> commonResult = null;
		try {
			commonResult = BeanContext.getBean(PingAnService.class).consume(tcd);
		}catch(Exception e) {
			throw new ServiceException(e.getMessage());
		}
		log.info("请求平安支付参数:{}, 结果: {}", JSONUtil.wrap(tcd, false), JSONUtil.wrap(commonResult, false));
		if (commonResult.getCode() == 200) {
			return JSONUtil.wrap(commonResult.getData().convert(consume.getOrderNo()), false).toString();
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
