package com.weesharing.pay.service.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.weesharing.pay.dto.AggregateRefund;
import com.weesharing.pay.dto.callback.WorkOrderCallBack;
import com.weesharing.pay.dto.paytype.PayType;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.entity.PreConsume;
import com.weesharing.pay.entity.PreRefund;
import com.weesharing.pay.entity.Refund;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.feign.BeanContext;
import com.weesharing.pay.feign.WorkOrderService;
import com.weesharing.pay.service.IConsumeService;
import com.weesharing.pay.service.IPreConsumeService;
import com.weesharing.pay.service.IPreRefundService;
import com.weesharing.pay.service.IRefundService;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RefundHandler {
	
	@Autowired
	private IConsumeService consumeService;
	
	@Autowired
	private IRefundService refundService;
	
	@Autowired
	private IPreConsumeService preConsumeService;
	
	@Autowired
	private IPreRefundService preRefundService;
	
	private ExecutorService executor = Executors.newCachedThreadPool() ;
	
	public String doRefund(AggregateRefund refund) {
		QueryWrapper<PreConsume> preConsumeQuery = new QueryWrapper<PreConsume>();
		preConsumeQuery.eq("order_no", refund.getOrderNo());
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
		preRefund.setTotalFee(refund.getRefundFee());
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
		unRefund =  Integer.parseInt(preConsume.getActPayFee()) - unRefund;
		if(unRefund >= Integer.parseInt(refund.getRefundFee())){
			return true;
		}
		
		return false;
	}
	
	private void asyncRefund(PreRefund preRefund, AggregateRefund refund) {
		executor.submit(new Runnable(){
			@Override
			public void run() {
				autoAllocationRefund(preRefund, refund);
			}
		});
	}
	
	/**
	 * 按退款优先级自动分配金额退款
	 * @param preRefund
	 * @param refund
	 * @param refundTotal
	 * @return
	 */
	private void autoAllocationRefund(PreRefund preRefund, AggregateRefund aggregateRefund) {
		//1: 成功, 2: 失败, 3: 部分失败, 0: 新创建, 4: 退款中
		Long refundTotal = Long.parseLong(aggregateRefund.getRefundFee());
		log.info("总退款金额: {}", refundTotal);
		Long remainTotal = 0L;
		boolean isAsync = false;
		if (refundTotal > 0) {
			List<Consume> consumes = autoAllocationRefundHandler(preRefund, aggregateRefund, refundTotal);
			for (Consume refund : consumes) {
				try {
					remainTotal = remainTotal + Long.parseLong(refund.getActPayFee());
					if(checkRefundType(refund.getPayType())) {
						refundService.doAsyncRefund(aggregateRefund.convert(preRefund, refund));
						isAsync = true;
					}else {
						refundService.doRefund(aggregateRefund.convert(preRefund, refund));
					}
					
				} catch (Exception e) {
					log.info("退款异常:{}", e.getMessage());
					remainTotal = remainTotal - Long.parseLong(refund.getActPayFee());
				}
			}
			
			//设置退款状态
			if(isAsync) {
				preRefund.setStatus(4);
			}else if(remainTotal == 0) {
				preRefund.setStatus(2);
			}else if(remainTotal > 0 && remainTotal < refundTotal) {
				preRefund.setStatus(3);
				preRefund.setRefundFee(String.valueOf(remainTotal));
			}else {
				preRefund.setStatus(1);
				preRefund.setRefundFee(String.valueOf(refundTotal));
			}
			preRefund.setTradeDate(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
			preRefund.insertOrUpdate();
		}
		
		//存在异步退款等待消息
		//不存在回调工单
		if(isAsync) {
			return;
		}else {
			WorkOrderCallBack result  = new WorkOrderCallBack(preRefund);
			refundNotifyHandler(result);
		}
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
		
		List<Consume> refunds = new ArrayList<Consume>();
		
		for(PayType refundType : PayType.values()) {
			if (refundTotal > 0) {
				QueryWrapper<Consume> consumeQuery = new QueryWrapper<Consume>();
				consumeQuery.eq("pay_type", refundType.getName());
				consumeQuery.eq("order_no", refund.getOrderNo());
				consumeQuery.eq("status", 1);
				List<Consume> consumes = consumeService.list(consumeQuery);
				Long payTotal = consumes.stream().mapToLong(pay -> Long.parseLong(pay.getActPayFee())).sum();
				
				QueryWrapper<Refund> refundQuery = new QueryWrapper<Refund>();
				refundQuery.eq("pay_type", refundType.getName());
				refundQuery.eq("order_no", refund.getOrderNo());
				refundQuery.eq("status", 1);
				List<Refund> refundeds = refundService.list(refundQuery);
				Long processTotal = refundeds.stream().mapToLong(refunded -> Long.parseLong(refunded.getRefundFee())).sum();
				
				//剩余已支付的款
				Long remainTotal = payTotal - processTotal;
				if(consumes != null && consumes.size() > 0 && refundTotal > 0 && remainTotal > 0) {
					
					log.info("[退款]{}总剩余退款金额: {}", refundType, remainTotal);
					
					for(Consume consume : consumes) {
						//核算某种支付方式的剩余款项
						Long remain = Long.parseLong(consume.getActPayFee()) ;
						for( Refund refunded:refundeds) {
							if(refunded.getCardNo().equals(consume.getCardNo())) {
								remain = remain - Long.parseLong(refunded.getRefundFee());
							}
						}
						
						if (refundTotal > 0 && remain > 0) {
							log.info("[退款] *** 开始回退支付的金额 *** ");
							
							if(refundTotal >= remain) {
								consume.setActPayFee(String.valueOf(remain));
								log.info("[退款] 退款: {}", remain);
								refundTotal = refundTotal - remain;
							}else {
								consume.setActPayFee(String.valueOf(refundTotal));
								log.info("[退款] 退款: {}", refundTotal);
								refundTotal = 0L;
							}
							
							refunds.add(consume);
						}
					}
				}
			}
		}
		return refunds;
	}
	
	private boolean checkRefundType(String payType) {
		 if(PayType.valueOf(payType.toUpperCase()).getRefund().equals("async")) {
			 return true;
		 }
		 return false;
	}
	

	/**
	 * 退款回调函数
	 * @param notifyUrl
	 * @param json
	 */
	public void refundNotifyHandler(WorkOrderCallBack result) {
		executor.submit(new Runnable(){
			@Override
			public void run() {
				log.info("退款回调, 参数: {}", JSONUtil.wrap(result, false).toString());
				BeanContext.getBean(WorkOrderService.class).refundNotify(result);
			}
		});
	}

}
