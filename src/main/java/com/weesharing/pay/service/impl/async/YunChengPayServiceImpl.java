package com.weesharing.pay.service.impl.async;

import org.springframework.stereotype.Service;

import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.entity.Refund;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.feign.param.YunChengConsumeData;
import com.weesharing.pay.service.IPayAsyncService;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("yunChengPayServiceImpl")
public class YunChengPayServiceImpl implements IPayAsyncService {

	@Override
	public String doPay(Consume consume) {
		YunChengConsumeData tcd = new YunChengConsumeData(consume);
		log.info("请求云城支付参数:{}", JSONUtil.wrap(tcd, false));
		return JSONUtil.wrap(tcd, false).toString();
	}

	@Override
	public String doRefund(Refund refund) {
		throw new ServiceException("[异步退款]:不支持此退款方式");
	}

}
