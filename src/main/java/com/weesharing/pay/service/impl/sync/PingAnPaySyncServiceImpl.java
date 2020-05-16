package com.weesharing.pay.service.impl.sync;

import java.util.Date;

import com.weesharing.pay.utils.AggPayTradeDate;
import org.springframework.stereotype.Service;

import com.weesharing.pay.common.CommonResult2;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.entity.Refund;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.feign.BeanContext;
import com.weesharing.pay.feign.PingAnService;
import com.weesharing.pay.feign.param.PingAnRefundData;
import com.weesharing.pay.feign.result.PingAnResult;
import com.weesharing.pay.service.IPaySyncService;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("pinganPaySyncService")
public class PingAnPaySyncServiceImpl implements IPaySyncService {

	@Override
	public void doPay(Consume consume) {
		throw new ServiceException("[同步支付]:不支持此支付方式");
	}

	@Override
	public void doRefund(Refund refund) {
		PingAnRefundData  trd = new PingAnRefundData(refund);
		CommonResult2<PingAnResult> commonResult = BeanContext.getBean(PingAnService.class).refund(trd);
		log.info("请求平安退款参数: {}, 结果: {}", JSONUtil.wrap(trd, false), JSONUtil.wrap(commonResult, false));
		if (commonResult.getCode() == 200) {
			refund.setTradeDate(AggPayTradeDate.buildTradeDate());
			if(null != commonResult.getData() && null != commonResult.getData().getOrderNo()) {
				refund.setTradeNo(commonResult.getData().getOrderNo());
			}
			refund.setStatus(1);
			refund.insertOrUpdate();
		} else if (commonResult.getCode() != 200) {
			refund.setStatus(2);
			refund.insertOrUpdate();
			throw new ServiceException(commonResult.getMsg());
		}
	}

}
