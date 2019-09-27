package com.weesharing.pay.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.weesharing.pay.common.CommonResult;
import com.weesharing.pay.dto.AggregatePay;
import com.weesharing.pay.dto.AggregateRefund;
import com.weesharing.pay.dto.PrePay;
import com.weesharing.pay.dto.PrePayResult;
import com.weesharing.pay.dto.QueryConsumeResult;
import com.weesharing.pay.dto.QueryRefundResult;
import com.weesharing.pay.dto.pay.WOAPay;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.feign.WOAService;
import com.weesharing.pay.feign.param.WOAConsumeData;
import com.weesharing.pay.feign.result.ConsumeResult;
import com.weesharing.pay.service.IConsumeService;
import com.weesharing.pay.service.IRefundService;
import com.weesharing.pay.service.PayService;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("woaPayService")
public class WOAPayServiceImpl implements PayService{
	
	@Autowired
	private IConsumeService consumeService;
	
	@Autowired
	private IRefundService refundService;
	
	@Autowired
	private WOAService woaService;
	
	@Override
	public PrePayResult prePay(PrePay prePay) {
		throw new ServiceException("调用错误");
	}

	@Override
	public String doPay(AggregatePay pay) {
		/**
		 * 	1. 查询支付订单号
		 *  1. 比对实际支付价格
		 *  2. 更新支付订单的字段信息
		 *  3. 调用联机账户
		 * 
		 */
		WOAPay woaPay = pay.getWoaPay();
		Date now = new Date();
		QueryWrapper<Consume> consumeQuery = new QueryWrapper<Consume>();
		consumeQuery.eq("order_no", woaPay.getOrderNo());
		consumeQuery.eq("act_pay_fee", woaPay.getActPayFee());
		consumeQuery.between("create_date", DateUtil.offsetMinute(now, -30) , now);
		
		Consume consume = consumeService.getOne(consumeQuery);
		if(consume == null) {
			throw new ServiceException("请核实支付订单号和支付金额再支付或者重新获取支付订单号");
		}else if(consume.getStatus() != 0){
			throw new ServiceException("该支付交易已处理过,请重新申请支付订单号");
		}
		consume.setPayType(woaPay.getPayType());
		consume.setCardNo(woaPay.getCardNo());
		consume.insertOrUpdate();
		
		// 调用联机账户
		WOAConsumeData tcd = new WOAConsumeData(woaPay);
		CommonResult<ConsumeResult> commonResult = woaService.consume(tcd);
		log.debug("请求联机账户支付参数:{}, 结果: {}", JSONUtil.wrap(tcd, false), JSONUtil.wrap(commonResult, false));
		if (commonResult.getCode() == 200) {
			consume.setTradeNo(commonResult.getData().getTradeNo());
			consume.setTradeDate(commonResult.getData().getTradeDate());
			consume.setStatus(1);
			consume.insertOrUpdate();
		} else if(commonResult.getCode() == 500) {
			consume.setStatus(2);
			consume.insertOrUpdate();
			throw new ServiceException(commonResult.getMessage());
		}
		
		return consume.getTradeNo();
	}

	@Override
	public List<QueryConsumeResult> doQuery(String orderNo) {
		throw new ServiceException("调用错误");
	}

	@Override
	public String doRefund(AggregateRefund aggregateRefund) {
////		QueryWrapper<Consume> consumeQuery = new QueryWrapper<Consume>();
////		consumeQuery.eq("order_no", aggregateRefund.getOrderNo());
////		consumeQuery.eq("status", 1);
////		Consume consume = consumeService.getOne(consumeQuery);
////		if(consume == null) {
////			throw new ServiceException("该退款没有此支付订单交易,请核实后重试.");
////		}
////		
////		QueryWrapper<Refund> refundQuery = new QueryWrapper<Refund>();
////		refundQuery.eq("out_refund_no", aggregateRefund.getOutRefundNo());
////		refundQuery.eq("order_no", consume.getOrderNo());
////		
////		Refund refund = refundService.getOne(refundQuery);
////		if(refund != null) {
////			throw new ServiceException("该退款已存在, 如果未退款成功,请更换退款单号重试.");
////		}
////		
////		refund = aggregateRefund.convert();
////		refund.setSourceOutTradeNo(consume.getOutTradeNo());
////		refund.setTotalFee(consume.getTotalFee());
////		refund.setCardNo(consume.getCardNo());
////		refund.setTradeNo(consume.getTradeNo());
////		refund.insert();
////		
////		// 调用联机账户
////		WOARefundData  trd = new WOARefundData(refund);
////		CommonResult<RefundResult> commonResult = woaService.refund(trd);
////		log.debug("请求联机账户退款参数: {}, 结果: {}", JSONUtil.wrap(trd, false), JSONUtil.wrap(commonResult, false));
////		if (commonResult.getCode() == 200) {
////			refund.setRefundNo(commonResult.getData().getRefundNo());
////			refund.setTradeDate(commonResult.getData().getTradeDate());
////			refund.setStatus(1);
////			refund.insertOrUpdate();
////		} else if (commonResult.getCode() == 500) {
////			refund.setStatus(2);
////			refund.insertOrUpdate();
////			throw new ServiceException(commonResult.getMessage());
////		}
//		
//		//回调
//		return refund.getRefundNo();
		return null;
	}

	@Override
	public List<QueryRefundResult> doRefundQuery(String orderNo) {
		throw new ServiceException("调用错误");
	}

	@Override
	public void doRefund(Consume balanceConsume) {
		// TODO Auto-generated method stub
		
	}

}
