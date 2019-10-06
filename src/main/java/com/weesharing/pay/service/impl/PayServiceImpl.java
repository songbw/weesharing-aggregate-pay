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
import com.weesharing.pay.dto.AggregatePay;
import com.weesharing.pay.dto.AggregateRefund;
import com.weesharing.pay.dto.BackBean;
import com.weesharing.pay.dto.BackRequest;
import com.weesharing.pay.dto.PrePay;
import com.weesharing.pay.dto.PrePayResult;
import com.weesharing.pay.dto.QueryConsumeResult;
import com.weesharing.pay.dto.QueryRefundResult;
import com.weesharing.pay.dto.pay.PayType;
import com.weesharing.pay.dto.pay.WOCPay;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.entity.PreConsume;
import com.weesharing.pay.entity.PreRefund;
import com.weesharing.pay.entity.Refund;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.service.IConsumeService;
import com.weesharing.pay.service.IPreConsumeService;
import com.weesharing.pay.service.IPreRefundService;
import com.weesharing.pay.service.IRefundService;
import com.weesharing.pay.service.PayService;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service(value = "payService")
public class PayServiceImpl implements PayService{
	
	@Autowired
	private IConsumeService consumeService;
	
	@Autowired
	private IRefundService refundService;
	
	@Autowired
	private IPreConsumeService preConsumeService;
	
	@Autowired
	private IPreRefundService preRefundService;
	
	private ExecutorService executor = Executors.newCachedThreadPool() ;
	
	@Override
	public PrePayResult prePay(PrePay prePay) {
		
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
		QueryWrapper<PreConsume> preConsumeQuery = new QueryWrapper<PreConsume>();
		preConsumeQuery.eq("out_trade_no", prePay.getOutTradeNo());
		preConsumeQuery.eq("act_pay_fee", prePay.getActPayFee());
		preConsumeQuery.eq("status", 1);
		PreConsume success = preConsumeService.getOne(preConsumeQuery);
		if(success != null) {
			log.debug("预支付订单号已支付, 预支付号:{}", success.getOrderNo());
			throw new ServiceException("该订单已支付");
		}
		QueryWrapper<PreConsume> preConsumeQuery1 = new QueryWrapper<PreConsume>();
		preConsumeQuery1.eq("out_trade_no", prePay.getOutTradeNo());
		preConsumeQuery1.eq("act_pay_fee", prePay.getActPayFee());
		preConsumeQuery1.eq("status", 0);
		preConsumeQuery1.orderByDesc("create_date");
		PreConsume preConsume = preConsumeService.getOne(preConsumeQuery1, false);
		if(preConsume != null) {
			if(isExpire(preConsume.getCreateDate())) {
				String orderNo = UUID.randomUUID().toString().replaceAll("-", "");
				preConsume  = prePay.convert();
				preConsume.setOrderNo(orderNo);
				preConsume.insert();
				log.debug("预支付订单号已过期, 新的预支付号:{}", orderNo);
				return new PrePayResult(orderNo, prePay.getOutTradeNo());
			}
			log.debug("预支付订单号已存在, 预支付号: {}", preConsume.getOrderNo());
			return new PrePayResult(preConsume.getOrderNo(), prePay.getOutTradeNo());
		}else {
			String orderNo = UUID.randomUUID().toString().replaceAll("-", "");
			preConsume  = prePay.convert();
			preConsume.setOrderNo(orderNo);
			preConsume.insert();
			log.debug("预支付订单号生成完成, 预支付号: {}", orderNo);
			return new PrePayResult(orderNo, prePay.getOutTradeNo());
		}
	}

	@Override
	public String doPay(AggregatePay pay) {
		/**
		 *  1. 检查预支付信息是否存在
		 * 	1. 与预支付信息进行对比总金额是否正确
		 *  2. 根据支付信息进行扣款处理
		 *  3. 结果状态处理
		 *     1: 成功
		 *     2: 失败  ==> 发起退款流程
		 *     3: 失败(超时)  超时情况默认失败 ==> 发起退款流程
		 */
		
		QueryWrapper<PreConsume> preConsumeQuery = new QueryWrapper<PreConsume>();
		preConsumeQuery.eq("order_no", pay.getOrderNo());
		preConsumeQuery.eq("status", 0);
		preConsumeQuery.orderByDesc("create_date");
		PreConsume preConsume = preConsumeService.getOne(preConsumeQuery, false);
		if(preConsume == null) {
			throw new ServiceException("请核实支付订单号和支付金额再支付或者重新获取支付订单号");
		}else if(preConsume.getStatus() != 0){
			throw new ServiceException("该支付交易已处理过,请重新申请支付订单号");
		}
		//判断总金额支付正确
		if(checkPayFee(preConsume, pay) == 1) {
			//金额正确进行异步支付
			asyncPay(preConsume, pay, true);
		}else if(checkPayFee(preConsume, pay) == 0) {
			//金额正确进行0元异步支付
			asyncPay(preConsume, pay, false);
		}else {
			throw new ServiceException("支付失败, 请核对支付金额");
		}
		return preConsume.getTradeNo();
	}
	
	/**
	 * 判断支付金额和预支付金额是否一致
	 * @param preConsume
	 * @param pay
	 * @return 1: 一致, 0: 一致,但是0元支付, 2: 不一致
	 */
	private int checkPayFee(PreConsume preConsume, AggregatePay pay) {
		
		Integer preActPayFee = Integer.parseInt(preConsume.getActPayFee());
		if(preActPayFee == 0) {
			return 0;
		}else {
			if(pay.getBalancePay() != null) {
				preActPayFee  = preActPayFee - Integer.parseInt(pay.getBalancePay().getActPayFee());
			}
			if(pay.getWocPays()!=null && pay.getWocPays().size() >0) {
				for(WOCPay wocPay : pay.getWocPays()){
					preActPayFee  = preActPayFee - Integer.parseInt(wocPay.getActPayFee());
				};
			}
			if(pay.getWoaPay() != null) {
				preActPayFee  = preActPayFee - Integer.parseInt(pay.getWoaPay().getActPayFee());
			}
			
			if(preActPayFee == 0) {
				return 1;
			}else {
				return 2;
			}
		}
	}
	
	
	private void asyncPay(PreConsume preConsume, AggregatePay pay, Boolean zeroPay) {
		
		executor.submit(new Runnable(){

			@Override
			public void run() {
				
				if(zeroPay) {
					
					try {
						if(pay.getBalancePay() != null) {
							consumeService.doPay(pay.getBalancePay().convert());
						}
						if(pay.getWocPays()!=null && pay.getWocPays().size() >0) {
							pay.getWocPays().stream().forEach(wocPay -> {
								consumeService.doPay(wocPay.convert());
							});
						}
						if(pay.getWoaPay() != null) {
							consumeService.doPay(pay.getWoaPay().convert());
						}
						preConsume.setStatus(1);
						log.info("聚合支付成功");
					}catch(Exception e) {
						log.error("支付失败: {}, 参数: {}", e.getMessage(), JSONUtil.wrap(pay.getWoaPay(), false).toString());
						preConsume.setStatus(2);
						preConsume.insertOrUpdate();
						if(pay.getBalancePay() != null) {
							log.info("[支付失败] *** 开始回退余额支付的金额 *** ");
							doRefund(new AggregateRefund(pay.getBalancePay()));
						}
						if(pay.getWocPays()!=null && pay.getWocPays().size() >0) {
							log.info("[支付失败] *** 开始回退惠民优选卡支付的金额 *** ");
							pay.getWocPays().stream().forEach(wocPay -> {
								doRefund(new AggregateRefund(wocPay));
							});
						}
						throw new ServiceException("支付失败:" + e.getMessage()) ;
					}
				}else {
					preConsume.setStatus(1);
					log.info("聚合0元支付成功");
				}
				preConsume.setTradeDate(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
				preConsume.insertOrUpdate();

				//回调
				payNotifyHandler(preConsume.getNotifyUrl(), JSONUtil.wrap(new BackRequest(new BackBean(preConsume)), false).toString());
				
			}});
	}

	@Override
	public List<QueryConsumeResult> doQuery(String orderNo) {
		QueryWrapper<Consume> consumeQuery = new QueryWrapper<Consume>();
		consumeQuery.eq("order_no", orderNo);
		List<Consume> consumes = consumeService.list(consumeQuery);
		List<QueryConsumeResult> results = new ArrayList<QueryConsumeResult>();
		for(Consume consume : consumes) {
			results.add(new QueryConsumeResult(consume));
		}
		return results;
	}

	@Override
	public String doRefund(AggregateRefund refund) {
		QueryWrapper<PreConsume> preConsumeQuery = new QueryWrapper<PreConsume>();
		preConsumeQuery.eq("order_no", refund.getOrderNo());
		preConsumeQuery.eq("status", 1);
		PreConsume preConsume = preConsumeService.getOne(preConsumeQuery);
		if(preConsume == null ) {
			throw new ServiceException("该退款没有此支付订单交易,请核实后重试.");
		}
		
		QueryWrapper<PreRefund> preRefundQuery = new QueryWrapper<PreRefund>();
		preRefundQuery.eq("out_refund_no", refund.getOutRefundNo());
		preRefundQuery.eq("order_no", refund.getOrderNo());
		
		PreRefund preRefund = preRefundService.getOne(preRefundQuery);
		if(preRefund != null ) {
			throw new ServiceException("该退款已存在, 如果未退款成功,请更换退款单号重试.");
		}
		
		preRefund = refund.convert();
		preRefund.setSourceOutTradeNo(preConsume.getOutTradeNo());
		preRefund.setTotalFee(preConsume.getTotalFee());
		preRefund.insert();
		
		//判断退款金额
		if(checkRefundFee(preConsume, refund)) {
			//金额正确进行异步退款
			asyncRefund(preRefund, refund);
		}else {
			throw new ServiceException("退款失败, 请核对退款金额");
		}
		
		return preRefund.getOutRefundNo();
	}
	
	/**
	 * 判断退款金额是否大于支付金额
	 * @param preConsume
	 * @param refund
	 * @return
	 */
	private boolean checkRefundFee(PreConsume preConsume, AggregateRefund refund) {
		/**
		 * 1. 对比退款金额和支付金额
		 * 2. 对比退款金额和待退款金额
		 */
		if(Integer.parseInt(preConsume.getActPayFee()) == Integer.parseInt(refund.getRefundFee())){
			return true;
		}
		
		int unRefund =  0;
		QueryWrapper<Refund> refundQuery = new QueryWrapper<Refund>();
		refundQuery.eq("order_no", refund.getOrderNo());
		refundQuery.eq("status", 1);
		List<Refund> refunds = refundService.list(refundQuery);
		for(Refund one : refunds) {
			unRefund =  unRefund + Integer.parseInt(one.getRefundFee());
		}
		
		if(unRefund >= Integer.parseInt(refund.getRefundFee())){
			return true;
		}
		
		return false;
	}
	
	private void asyncRefund(PreRefund preRefund, AggregateRefund refund) {
		
		executor.submit(new Runnable(){

			@Override
			public void run() {
				
				int refundStatus = autoAllocationRefund(preRefund, refund);
				
				//设置退款状态
				preRefund.setStatus(refundStatus);
				preRefund.setTradeDate(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
				
				//回调
				refundNotifyHandler(refund.getNotifyUrl(), JSONUtil.wrap(new QueryRefundResult(preRefund), false).toString());
			}});
	}
	
	/**
	 * 按退款优先级自动分配金额退款
	 * @param preRefund
	 * @param refund
	 * @param refundTotal
	 * @return
	 */
	private int autoAllocationRefund(PreRefund preRefund, AggregateRefund aggregateRefund) {
		int refundStatus = 0;
		
		Long refundTotal = Long.parseLong(aggregateRefund.getRefundFee());
		log.info("总退款金额: {}", refundTotal);
		
		if (refundTotal > 0) {
			for (Consume refund : autoAllocationRefundHandler(preRefund, aggregateRefund, refundTotal)) {
				try {
					refundService.doRefund(aggregateRefund.conver(preRefund, refund));
				} catch (Exception e) {
					refundStatus = 2;
				}
			}
		}else {
			refundStatus = 2;
		}
		return refundStatus;
	}
	
	/**
	 * 退款自动随机分配金额处理器
	 * @param preRefund
	 * @param refund
	 * @param refundTotal
	 * @param payType
	 * @return
	 */
	private List<Consume> autoAllocationRefundHandler(PreRefund preRefund, AggregateRefund refund, Long refundTotal) {
		
		String refundTypes[] = {PayType.BALANCE.getName(), PayType.CARD.getName(), PayType.WOA.getName()};
		List<Consume> refunds = new ArrayList<Consume>();
		
		for(String refundType: refundTypes) {
			if (refundTotal > 0) {
				QueryWrapper<Consume> consumeQuery = new QueryWrapper<Consume>();
				consumeQuery.eq("pay_type", refundType);
				consumeQuery.eq("order_no", refund.getOrderNo());
				consumeQuery.eq("status", 1);
				List<Consume> consumes = consumeService.list(consumeQuery);
				
				if(consumes != null && consumes.size() > 0) {
					Long payTotal = consumes.stream().mapToLong(card -> Long.parseLong(card.getActPayFee())).sum();
					log.info("[退款]{}支付金额: {}", refundType, payTotal);
					
					for(Consume consume : consumes) {
						if (refundTotal > 0) {
							if(refundTotal > Long.parseLong(consume.getActPayFee())) {
								log.info("[退款]退款: {}", consume.getActPayFee());
								log.info("[退款] *** 开始回退支付的金额 *** ");
								refunds.add(consume);
								refundTotal = refundTotal - payTotal;
							}else {
								log.info("[部分退款] 部分退款: {}", consume.getActPayFee());
								log.info("[退款] *** 开始回退支付的金额 *** ");
								consume.setActPayFee(refund.getRefundFee());
								refunds.add(consume);
								refundTotal = 0L;
							}
						}
					}
				}
			}
		}
		return refunds;
	}
	

	@Override
	public List<QueryRefundResult> doRefundQuery(String orderNo) {
		QueryWrapper<PreRefund> refundQuery = new QueryWrapper<PreRefund>();
		refundQuery.eq("order_no", orderNo);
		List<PreRefund> refunds = preRefundService.list(refundQuery);
		List<QueryRefundResult> results = new ArrayList<QueryRefundResult>();
		for(PreRefund refund: refunds) {
			results.add(new QueryRefundResult(refund));
		}
		return results;
	}

	/**
	 * 支付回调函数
	 * @param notifyUrl
	 * @param json
	 */
	private void payNotifyHandler(String notifyUrl, String json) {
		executor.submit(new Runnable(){
			@Override
			public void run() {
				log.debug("支付回调, 准备回调地址:{}, 参数: {}", notifyUrl, json);
				HttpUtil.post(notifyUrl, json);
			}
		});
	}

	/**
	 * 退款回调函数
	 * @param notifyUrl
	 * @param json
	 */
	private void refundNotifyHandler(String notifyUrl, String json) {
		executor.submit(new Runnable(){
			@Override
			public void run() {
				log.debug("退款回调, 准备回调地址:{}, 参数: {}", notifyUrl, json);
				HttpUtil.post(notifyUrl, json);
			}
		});
	}
	
	/**
	 * 交易是否过期
	 * @param createDate
	 * @return
	 */
	private boolean isExpire(LocalDateTime createDate) {
		Date db = Date.from(createDate.atZone(ZoneId.systemDefault()).toInstant());
		if(db.before(DateUtil.offsetMinute(new Date(), -30))) {
			return true;
		}
		return false;
	}
}
