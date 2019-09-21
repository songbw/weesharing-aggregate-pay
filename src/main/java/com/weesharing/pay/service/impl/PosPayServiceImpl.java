package com.weesharing.pay.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.weesharing.pay.common.CommonResult;
import com.weesharing.pay.dto.BackBean;
import com.weesharing.pay.dto.BackRequest;
import com.weesharing.pay.dto.ConsumeResultDTO;
import com.weesharing.pay.dto.PayDTO;
import com.weesharing.pay.dto.PrePayDTO;
import com.weesharing.pay.dto.PrePayResultDTO;
import com.weesharing.pay.dto.RefundDTO;
import com.weesharing.pay.dto.RefundResultDTO;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.entity.Refund;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.feign.WOAService;
import com.weesharing.pay.feign.param.TradeConsumeData;
import com.weesharing.pay.feign.param.TradeRefundData;
import com.weesharing.pay.feign.result.ConsumeResult;
import com.weesharing.pay.feign.result.RefundResult;
import com.weesharing.pay.service.IConsumeService;
import com.weesharing.pay.service.IRefundService;
import com.weesharing.pay.service.PayService;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PosPayServiceImpl implements PayService{
	
	@Autowired
	private IConsumeService consumeService;
	
	@Autowired
	private IRefundService refundService;
	
	@Autowired
	private WOAService woaService;
	
	private ExecutorService executor = Executors.newCachedThreadPool() ;
	
	@Override
	public PrePayResultDTO prePay(PrePayDTO prePay) {
		
		/**
		 * 1. 查询最后一个预支付记录是否存在
		 * 2. 检查该订单是否已支付
		 * 2. 如果存在:
		 * 		检查是否过期[30分钟]
		 * 			   未过期:  直接返回预支付号 
		 * 			   已过期:  生成新的预支付号
		 * 3.如果不存在: 
		 * 		生成新的预支付号
		 */
		QueryWrapper<Consume> consumeQuery = new QueryWrapper<Consume>();
		consumeQuery.eq("out_trade_no", prePay.getOutTradeNo());
		consumeQuery.eq("act_pay_fee", prePay.getActPayFee());
		consumeQuery.eq("status", 1);
		Consume success = consumeService.getOne(consumeQuery);
		if(success != null) {
			log.debug("预支付订单号已支付, 预支付号:{}", success.getOrderNo());
			throw new ServiceException("该订单已支付");
		}
		QueryWrapper<Consume> consumeQuery1 = new QueryWrapper<Consume>();
		consumeQuery1.eq("out_trade_no", prePay.getOutTradeNo());
		consumeQuery1.eq("act_pay_fee", prePay.getActPayFee());
		consumeQuery1.eq("status", 0);
		consumeQuery1.orderByDesc("create_date");
		Consume consume = consumeService.getOne(consumeQuery1, false);
		if(consume != null) {
			if(isExpire(consume.getCreateDate())) {
				String orderNo = UUID.randomUUID().toString();
				consume  = prePay.convert();
				consume.setOrderNo(orderNo);
				consume.insert();
				log.debug("预支付订单号已过期, 新的预支付号:{}", orderNo);
				return new PrePayResultDTO(orderNo, prePay.getOutTradeNo());
			}
			log.debug("预支付订单号已存在, 预支付号: {}", consume.getOrderNo());
			return new PrePayResultDTO(consume.getOrderNo(), prePay.getOutTradeNo());
		}else {
			String orderNo = UUID.randomUUID().toString();
			consume  = prePay.convert();
			consume.setOrderNo(orderNo);
			consume.insert();
			log.debug("预支付订单号生成完成, 预支付号: {}", orderNo);
			return new PrePayResultDTO(orderNo, prePay.getOutTradeNo());
		}
	}

	@Override
	public String doPay(PayDTO pay) {
		/**
		 * 	1. 查询支付订单号
		 *  1. 比对实际支付价格
		 *  2. 更新支付订单的字段信息
		 *  3. 调用联机账户
		 * 
		 */
		Date now = new Date();
		QueryWrapper<Consume> consumeQuery = new QueryWrapper<Consume>();
		consumeQuery.eq("order_no", pay.getOrderNo());
		consumeQuery.eq("act_pay_fee", pay.getActPayFree());
		consumeQuery.between("create_date", DateUtil.offsetMinute(now, -30) , now);
		
		Consume consume = consumeService.getOne(consumeQuery);
		if(consume == null) {
			throw new ServiceException("请核实支付订单号和支付金额再支付或者重新获取支付订单号");
		}else if(consume.getStatus() != 0){
			throw new ServiceException("该支付交易已处理过,请重新申请支付订单号");
		}
		consume.setPayType(pay.getPayType());
		consume.setCardNo(pay.getCardNo());
		consume.setCardPwd(pay.getCardPwd());
		consume.insertOrUpdate();
		
		// 调用联机账户
		TradeConsumeData tcd = new TradeConsumeData(pay);
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
		
		//回调
		payNotifyHandler(consume.getNotifyUrl(), JSONUtil.wrap(new BackRequest(new BackBean(consume)), false).toString());
		return consume.getTradeNo();
	}

	@Override
	public List<ConsumeResultDTO> doQuery(String orderNo) {
		QueryWrapper<Consume> consumeQuery = new QueryWrapper<Consume>();
		consumeQuery.eq("order_no", orderNo);
		List<Consume> consumes = consumeService.list(consumeQuery);
		List<ConsumeResultDTO> results = new ArrayList<ConsumeResultDTO>();
		for(Consume consume : consumes) {
			results.add(new ConsumeResultDTO(consume));
		}
		return results;
	}

	@Override
	public String doRefund(RefundDTO refundDTO) {
		QueryWrapper<Consume> consumeQuery = new QueryWrapper<Consume>();
		consumeQuery.eq("order_no", refundDTO.getOrderNo());
		consumeQuery.eq("status", 1);
		Consume consume = consumeService.getOne(consumeQuery);
		if(consume == null) {
			throw new ServiceException("该退款没有此支付订单交易,请核实后重试.");
		}
		
		QueryWrapper<Refund> refundQuery = new QueryWrapper<Refund>();
		refundQuery.eq("out_refund_no", refundDTO.getOutRefundNo());
		refundQuery.eq("order_no", consume.getOrderNo());
		
		Refund refund = refundService.getOne(refundQuery);
		if(refund != null) {
			throw new ServiceException("该退款已存在, 如果未退款成功,请更换退款单号重试.");
		}
		
		refund = refundDTO.convert();
		refund.setSourceOutTradeNo(consume.getOutTradeNo());
		refund.setTotalFee(consume.getTotalFee());
		refund.setCardNo(consume.getCardNo());
		refund.setCardPwd(consume.getCardPwd());
		refund.setTradeNo(consume.getTradeNo());
		refund.insert();
		
		// 调用联机账户
		TradeRefundData  trd = new TradeRefundData(refund);
		CommonResult<RefundResult> commonResult = woaService.refund(trd);
		log.debug("请求联机账户退款参数: {}, 结果: {}", JSONUtil.wrap(trd, false), JSONUtil.wrap(commonResult, false));
		if (commonResult.getCode() == 200) {
			refund.setRefundNo(commonResult.getData().getRefundNo());
			refund.setTradeDate(commonResult.getData().getTradeDate());
			refund.setStatus(1);
			refund.insertOrUpdate();
		} else if (commonResult.getCode() == 500) {
			refund.setStatus(2);
			refund.insertOrUpdate();
			throw new ServiceException(commonResult.getMessage());
		}
		
		//回调
		refundNotifyHandler(refund.getNotifyUrl(), JSONUtil.wrap(new RefundResultDTO(refund), false).toString());
		return refund.getRefundNo();
	}

	@Override
	public List<RefundResultDTO> doRefundQuery(String orderNo) {
		QueryWrapper<Refund> refundQuery = new QueryWrapper<Refund>();
		refundQuery.eq("order_no", orderNo);
		List<Refund> refunds = refundService.list(refundQuery);
		List<RefundResultDTO> results = new ArrayList<RefundResultDTO>();
		for(Refund refund: refunds) {
			results.add(new RefundResultDTO(refund));
		}
		return results;
	}

	@Override
	public void payNotifyHandler(String notifyUrl, String json) {
		executor.submit(new Runnable(){
			@Override
			public void run() {
				log.debug("支付回调, 准备回调地址:{}", notifyUrl);
				HttpUtil.post(notifyUrl, json);
			}
		});
	}

	@Override
	public void refundNotifyHandler(String notifyUrl, String json) {
		executor.submit(new Runnable(){
			@Override
			public void run() {
				log.debug("退款回调, 准备回调地址:{}", notifyUrl);
				HttpUtil.post(notifyUrl, json);
			}
		});
	}
	
	private boolean isExpire(LocalDateTime createDate) {
		Date db = Date.from(createDate.atZone(ZoneId.systemDefault()).toInstant());
		if(db.before(DateUtil.offsetMinute(new Date(), -30))) {
			return true;
		}
		return false;
	}

}
