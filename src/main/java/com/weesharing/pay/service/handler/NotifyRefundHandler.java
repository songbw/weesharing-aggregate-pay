package com.weesharing.pay.service.handler;

import java.util.List;

import cn.hutool.json.JSONUtil;
import com.weesharing.pay.utils.AggPayTradeDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.weesharing.pay.dto.callback.WorkOrderCallBack;
import com.weesharing.pay.dto.notify.CommonRefundNotify;
import com.weesharing.pay.dto.paytype.PayType;
import com.weesharing.pay.entity.PreRefund;
import com.weesharing.pay.entity.Refund;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.service.IPreRefundService;
import com.weesharing.pay.service.IRefundService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotifyRefundHandler {

	@Autowired
	private IPreRefundService preRefundService;

	@Autowired
	private IRefundService refundService;

	@Autowired
	private RefundHandler refundHandler;

	public void RefundNotifyService(CommonRefundNotify refundNotify) {
		log.info("[退款回调] 入参:{}", JSONUtil.toJsonStr(refundNotify));
		//更新异步退款状态
		Refund refund = refundService.getOne(getRefund(refundNotify));
		if (refund == null) {
			throw new ServiceException("该退款不存在");
		}

		if(refund.getStatus() != 0) {
			throw new ServiceException("该退款已处理完成.");
		}

		log.info("[退款回调][支付号]:{}, [金额]:{}", refundNotify.getOrderNo(),  refundNotify.getRefundFee());

		refund.setStatus(1);
		refund.setTradeDate(AggPayTradeDate.buildTradeDate(refundNotify.getTradeDate()));
		refund.insertOrUpdate();

		changeParam(refundNotify, refund);

		//通知工单
		notifyWorkOrder(refundNotify);
	}

	/**
	 * 更新退款状态并回调工单系统
	 * @param refundNotify
	 */
	private void notifyWorkOrder(CommonRefundNotify refundNotify) {
		log.info("更新退款状态并回调工单系统  入参: {}",JSONUtil.toJsonStr(refundNotify));
		//查询预退款请求
		QueryWrapper<PreRefund> preRefundQuery = new QueryWrapper<PreRefund>();
		preRefundQuery.eq("order_no", refundNotify.getOrderNo());
		preRefundQuery.eq("out_refund_no", refundNotify.getRefundNo());

		PreRefund preRefund = preRefundService.getOne(preRefundQuery);
		if (preRefund == null) {
			throw new ServiceException("该退款不存在.");
		}

		//查询退款成功记录
		int success = 0;
		QueryWrapper<Refund> refundQuery = new QueryWrapper<Refund>();
		refundQuery.eq("order_no", refundNotify.getOrderNo());
		refundQuery.eq("out_refund_no", refundNotify.getRefundNo());

		List<Refund> refunds = refundService.list(refundQuery);
		for(Refund refund : refunds) {
			if(refund.getStatus() == 1) {
				success = success + 1;
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
		preRefund.setTradeDate(AggPayTradeDate.buildTradeDate(refundNotify.getTradeDate()));
		preRefund.insertOrUpdate();

		// 回调工单
		WorkOrderCallBack result = new WorkOrderCallBack(preRefund);
		refundHandler.refundNotifyHandler(result);

	}

	/**
	 * 更改回调参数
	 * @param refundNotify
	 * @param refund
	 */
	private void changeParam(CommonRefundNotify refundNotify, Refund refund) {
		if(refundNotify.getPayType().equals(PayType.BANK.getName())) {
			refundNotify.setOrderNo(refund.getOrderNo());
			refundNotify.setRefundNo(refund.getOutRefundNo());
		}

	}

	private QueryWrapper<Refund> getRefund(CommonRefundNotify refundNotify){
		QueryWrapper<Refund> refundQuery = new QueryWrapper<Refund>();

		refundQuery.eq("pay_type", refundNotify.getPayType());

		if(refundNotify.getPayType().equals(PayType.BANK.getName())) {
			refundQuery.eq("refund_no", refundNotify.getRefundNo());
//		}else if(refundNotify.getPayType().equals(PayType.FCWXXCX.getName())) {
//			refundQuery.eq("refund_no", refundNotify.getTradeNo());
//			refundQuery.eq("order_no", refundNotify.getOrderNo());
//			refundQuery.eq("out_refund_no", refundNotify.getRefundNo());
		}else {
			refundQuery.eq("order_no", refundNotify.getOrderNo());
			refundQuery.eq("out_refund_no", refundNotify.getRefundNo());
		}

		return refundQuery;
	}



}
