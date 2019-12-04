package com.weesharing.pay.service.handler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.weesharing.pay.dto.callback.WorkOrderCallBack;
import com.weesharing.pay.dto.notify.CommonRefundNotify;
import com.weesharing.pay.entity.PreRefund;
import com.weesharing.pay.entity.Refund;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.service.IPreRefundService;
import com.weesharing.pay.service.IRefundService;

@Service
public class NotifyRefundHandler {

	@Autowired
	private IPreRefundService preRefundService;
	
	@Autowired
	private IRefundService refundService;

	@Autowired
	private RefundHandler refundHandler;

	public void RefundNotifyService(CommonRefundNotify refundNotify) {
		
		//更新异步退款状态
		QueryWrapper<Refund> refundQuery = new QueryWrapper<Refund>();
		refundQuery.eq("order_no", refundNotify.getOrderNo());
		refundQuery.eq("out_refund_no", refundNotify.getRefundNo());
		refundQuery.eq("pay_type", refundNotify.getPayType());

		Refund refund = refundService.getOne(refundQuery);
		if (refund == null) {
			throw new ServiceException("该退款不存在");
		}
		
		if(refund.getStatus() != 0) {
			throw new ServiceException("该退款已处理完成.");
		}
		
		refund.setStatus(1);
		refund.insertOrUpdate();
		
		//通知工单
		NotifyWorkOrder(refundNotify);
	}

	/**
	 * 更新退款状态并回调工单系统
	 * @param orderNo
	 */
	private void NotifyWorkOrder(CommonRefundNotify refundNotify) {
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
		preRefund.insertOrUpdate();
		
		// 回调工单
		WorkOrderCallBack result = new WorkOrderCallBack(preRefund);
		refundHandler.refundNotifyHandler(result);

	}
	
	

}
