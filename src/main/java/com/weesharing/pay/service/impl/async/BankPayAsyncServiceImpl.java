package com.weesharing.pay.service.impl.async;

import org.springframework.stereotype.Service;

import com.weesharing.pay.common.CommonResult2;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.entity.Refund;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.feign.BeanContext;
import com.weesharing.pay.feign.FastBankPayService;
import com.weesharing.pay.feign.param.BankRefundData;
import com.weesharing.pay.service.IPayAsyncService;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service(value = "bankPayAsyncService")
public class BankPayAsyncServiceImpl implements IPayAsyncService {
	
	@Override
	public String doPay(Consume consume) {
		throw new ServiceException("[异步支付]不支持此支付");
	}
	
	@Override
	public String doRefund(Refund refund) {
		// 调用快捷支付
		BankRefundData trd = new BankRefundData(refund);
		CommonResult2<String> commonResult = BeanContext.getBean(FastBankPayService.class).refund(trd);
		log.info("请求快捷支付退款参数: {}, 结果: {}", JSONUtil.wrap(trd, false), JSONUtil.wrap(commonResult, false));
		if (commonResult.getCode() == 200) {
			refund.setRefundNo(commonResult.getData());
			refund.insertOrUpdate();
		} else if (commonResult.getCode() != 200) {
			refund.setStatus(2);
			refund.insertOrUpdate();
			throw new ServiceException(commonResult.getMsg());
		}
		return null;
	}

}
