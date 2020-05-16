package com.weesharing.pay.service.impl;

import java.util.Date;
import java.util.List;

import com.weesharing.pay.utils.AggPayTradeDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.weesharing.pay.common.CommonResult2;
import com.weesharing.pay.dto.callback.WorkOrderCallBack;
import com.weesharing.pay.dto.paytype.PayType;
import com.weesharing.pay.entity.PreRefund;
import com.weesharing.pay.entity.Refund;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.feign.FastBankPayService;
import com.weesharing.pay.feign.result.ZTKXCAS008ResponseBean;
import com.weesharing.pay.service.IPreRefundService;
import com.weesharing.pay.service.IRefundService;
import com.weesharing.pay.service.handler.RefundHandler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class QueryPayStatusSchedule {

	@Autowired
	private IPreRefundService preRefundService;

	@Autowired
	private IRefundService refundService;

	@Autowired
	private RefundHandler refundHandler;

	@Autowired
	private FastBankPayService fastBankPayService;

	@Scheduled(cron="0 3 * * * ?")
//	@Scheduled(cron="*/5 * * * * ?")
	public void queryService() {

		QueryWrapper<Refund> refundQuery = new QueryWrapper<Refund>();
		refundQuery.eq("pay_type", PayType.BANK.getName());
		refundQuery.eq("status", 0);

		List<Refund> refunds = refundService.list(refundQuery);
		for(Refund refund : refunds) {
			ZTKXCAS008ResponseBean bean = getBankResult(refund.getRefundNo());
			int status = getBankStatus(bean);
			if(status > 0) {
				refund.setStatus(1);
				refund.setTradeDate(AggPayTradeDate.buildTradeDate(bean.getFinishTime()));
				refund.insertOrUpdate();
				callback(refund.getOrderNo(), refund.getOutRefundNo());
			}
		}
	}

	private void callback(String orderNo, String outRefundNo) {
		//查询预退款请求
		QueryWrapper<PreRefund> preRefundQuery = new QueryWrapper<PreRefund>();
		preRefundQuery.eq("order_no", orderNo);
		preRefundQuery.eq("out_refund_no", outRefundNo);

		PreRefund preRefund = preRefundService.getOne(preRefundQuery);
		if (preRefund == null) {
			throw new ServiceException("该退款不存在.");
		}

		//查询退款成功记录
		int success = 0;
		QueryWrapper<Refund> refundQuery = new QueryWrapper<Refund>();
		refundQuery.eq("order_no", orderNo);
		refundQuery.eq("out_refund_no", outRefundNo);

		List<Refund> refunds = refundService.list(refundQuery);
		for(Refund refund : refunds) {
			if(refund.getStatus() == 1) {
				success = success + 1;
			}

			if(refund.getStatus() == 0) {
				return ;
			}
		}

		//验证退款成功笔数
		if(success == 0 ) {
			preRefund.setStatus(2);
		}else if(success == refunds.size()) {
			preRefund.setStatus(1);
		}else {
			preRefund.setStatus(3);
		}
		preRefund.setTradeDate(AggPayTradeDate.buildTradeDate());
		preRefund.insertOrUpdate();

		// 回调工单
		WorkOrderCallBack result = new WorkOrderCallBack(preRefund);
		refundHandler.refundNotifyHandler(result);
	}


	private ZTKXCAS008ResponseBean getBankResult(String refundNo) {
		CommonResult2<ZTKXCAS008ResponseBean> bankResult = fastBankPayService.refundStatus(refundNo);
		log.info(JSONUtil.wrap(bankResult, false).toString());
		return bankResult.getData();
	}

	private int getBankStatus(ZTKXCAS008ResponseBean bean) {
		if(bean != null) {
			String status = bean.getOriTranStatus();
			if(status.equals("40")){
				return 1;
			}else if(status.equals("41")){
				return 2;
			}

		}
		return 0;
	}

}
