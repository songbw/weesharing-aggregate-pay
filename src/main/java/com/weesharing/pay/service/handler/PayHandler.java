package com.weesharing.pay.service.handler;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.weesharing.pay.dto.AggregatePay;
import com.weesharing.pay.dto.AggregateRefund;
import com.weesharing.pay.dto.callback.OrderCallBack;
import com.weesharing.pay.dto.callback.OrderCallBackData;
import com.weesharing.pay.dto.pay.WOCPay;
import com.weesharing.pay.dto.paytype.PayType;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.entity.PreConsume;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.service.IConsumeService;
import com.weesharing.pay.service.IPreConsumeService;
import com.weesharing.pay.service.RedisService;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PayHandler {
	
	@Autowired
	private IConsumeService consumeService;
	
	@Autowired
	private IPreConsumeService preConsumeService;
	
	@Autowired
	private RedisService redisService;
	
	@Autowired
	private RefundHandler refundHandler;
	
	private ExecutorService executor = Executors.newCachedThreadPool() ;
	
	/**
	 *  1. 检查预支付信息是否存在
	 * 	1. 与预支付信息进行对比总金额是否正确
	 *  2. 根据支付信息进行扣款处理
	 *  3. 结果状态处理
	 *     1: 成功
	 *     2: 失败  ==> 发起退款流程
	 *     3: 失败(超时)  超时情况默认失败 ==> 发起退款流程
	 */
	public String doPay(AggregatePay pay) {
		String pay_process = redisService.get("pay_process:" + pay.getOrderNo());
		if(StringUtils.isNotEmpty(pay_process)) {
			throw new ServiceException("该支付交易正在处理中, 请稍等.");
		}
		PreConsume preConsume = getPreConsume(pay.getOrderNo());
		//设置支付订单为处理中的状态
		redisService.set("pay_process:" + pay.getOrderNo(), pay.getOrderNo(), 30);
		//判断总金额支付正确
		int checkResult = checkPayFee(preConsume, pay);
		if(checkResult == 1) {
			//金额正确进行异步支付
			String asyncPayType = redisService.get("paytype:" + pay.getOrderNo());
			if(asyncPayType != null){
				return asyncPay(pay.getOrderNo(), asyncPayType);
			}else {
				syncPay(pay.getOrderNo(), true);
			}
		}else if( checkResult == 0) {
			//金额正确进行0元异步支付
			syncPay(pay.getOrderNo(), false);
		}else {
			redisService.remove("pay_process:" + pay.getOrderNo());
			throw new ServiceException("支付失败, 请核对支付金额");
		}
		return null;
	}
	
	/**
	 * 异步支付
	 * @param orderNo
	 * @param payType
	 * @return
	 */
	public String asyncPay(String orderNo, String payType) {
		return consumeService.doAsynPay(getConsume(orderNo, payType));
	}
	
	/**
	 * 同步支付
	 * @param orderNo
	 * @param zeroPay
	 */
	public void syncPay(String orderNo, Boolean zeroPay) {
		executor.submit(new Runnable(){
			@Override
			public void run() {
				PreConsume preConsume = getPreConsume(orderNo);
				if(zeroPay) {
					try {
						for(PayType payType: PayType.values()) {
							if(payType.getPay().equals("sync")) {
								for(Consume consume : getConsumeList(orderNo, payType.getName())) {
									consumeService.doPay(consume);
								}
							}
						}
					}catch(Exception e) {
						e.printStackTrace();
						log.error("支付失败: {}", e.getMessage());
						preConsume.setStatus(2);
						preConsume.insertOrUpdate();
						log.info("[支付失败] *** 开始回退余额支付的金额 *** ");
						refundHandler.doRefund(new AggregateRefund(preConsume));
						throw new ServiceException("支付失败:" + e.getMessage()) ;
					}
					preConsume.setStatus(1);
					log.info("聚合支付成功");
				}else {
					preConsume.setStatus(1);
					log.info("聚合0元支付成功");
				}
				preConsume.setTradeDate(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
				preConsume.insertOrUpdate();

				//回调
				payNotifyHandler(preConsume.getNotifyUrl(), JSONUtil.wrap(new OrderCallBack(new OrderCallBackData(preConsume)), false).toString());
				
			}});
	}
	
	/**
	 * 支付回调函数
	 * @param notifyUrl
	 * @param json
	 */
	private void payNotifyHandler(String notifyUrl, String json) {
		log.info("支付成功, 准备回调...");
		log.info("回调地址:{}, 参数: {}", notifyUrl, json);
		executor.submit(new Runnable(){
			@Override
			public void run() {
				HttpUtil.post(notifyUrl, json);
			}
		});
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
			//计算支付金额
			//持久化消费记录
			//检查是否异步支付
			if(pay.getBalancePay() != null) {
				preActPayFee  = computePrePayFee(preActPayFee , pay.getBalancePay().getActPayFee());
				consumeService.persistConsume(pay.getBalancePay().convert());
				checkPayType(preConsume.getOrderNo(), pay.getBalancePay().getPayType());
				
			}
			if(pay.getWocPays()!=null && pay.getWocPays().size() >0) {
				for(WOCPay wocPay : pay.getWocPays()){
					preActPayFee  = computePrePayFee(preActPayFee, wocPay.getActPayFee());
					consumeService.persistConsume(wocPay.convert());
					checkPayType(preConsume.getOrderNo(),wocPay.getPayType());
				};
			}
			if(pay.getWoaPay() != null) {
				preActPayFee  = computePrePayFee(preActPayFee, pay.getWoaPay().getActPayFee());
				consumeService.persistConsume(pay.getWoaPay().convert());
				checkPayType(preConsume.getOrderNo(), pay.getWoaPay().getPayType());
			}
			if(pay.getBankPay() != null) {
				preActPayFee  = computePrePayFee(preActPayFee, pay.getBankPay().getActPayFee());
				consumeService.persistConsume(pay.getBankPay().convert());
				redisService.set("bank_pay:" + pay.getOrderNo(), JSONUtil.wrap(pay.getBankPay(), false).toString());
				checkPayType(preConsume.getOrderNo(), pay.getBankPay().getPayType());
			}
			if(pay.getFcAlipayPay() != null) {
				preActPayFee  = computePrePayFee(preActPayFee, pay.getFcAlipayPay().getActPayFee());
				consumeService.persistConsume(pay.getFcAlipayPay().convert());
				checkPayType(preConsume.getOrderNo(), pay.getFcAlipayPay().getPayType());
			}
			if(pay.getFcWxPay() != null) {
				preActPayFee  = computePrePayFee(preActPayFee, pay.getFcWxPay().getActPayFee());
				consumeService.persistConsume(pay.getFcWxPay().convert());
				checkPayType(preConsume.getOrderNo(), pay.getFcWxPay().getPayType());
			}
			if(pay.getFcWxH5Pay() != null) {
				preActPayFee  = computePrePayFee(preActPayFee, pay.getFcWxH5Pay().getActPayFee());
				consumeService.persistConsume(pay.getFcWxH5Pay().convert());
				checkPayType(preConsume.getOrderNo(), pay.getFcWxH5Pay().getPayType());
			}
			if(pay.getFcWxXcxPay() != null) {
				preActPayFee  = computePrePayFee(preActPayFee, pay.getFcWxXcxPay().getActPayFee());
				consumeService.persistConsume(pay.getFcWxXcxPay().convert());
				checkPayType(preConsume.getOrderNo(), pay.getFcWxXcxPay().getPayType());
			}
			if(pay.getPingAnPay() != null) {
				preActPayFee  = computePrePayFee(preActPayFee, pay.getPingAnPay().getActPayFee());
				consumeService.persistConsume(pay.getPingAnPay().convert());
				checkPayType(preConsume.getOrderNo(), pay.getPingAnPay().getPayType());
			}
			
			if(preActPayFee == 0) {
				return 1;
			}else {
				return 2;
			}
		}
	}
	
	private Integer computePrePayFee(Integer preActPayFee, String payFee) {
		try {
			return preActPayFee - Integer.parseInt(payFee);
		}catch(Exception e) {
			throw new ServiceException("金额参数有误");
		}
	}
	
	private void checkPayType(String orderNo, String payType) {
		 if(PayType.valueOf(payType.toUpperCase()).getPay().equals("async")) {
			 redisService.set("paytype:" + orderNo, payType, 30 * 60);
		 }
	}
	
	private PreConsume getPreConsume(String orderNo) {
		QueryWrapper<PreConsume> preConsumeQuery = new QueryWrapper<PreConsume>();
		preConsumeQuery.eq("order_no", orderNo);
		PreConsume preConsume = preConsumeService.getOne(preConsumeQuery, false);
		if(preConsume == null) {
			throw new ServiceException("请核实支付订单号和支付金额再支付或者重新获取支付订单号");
		}else if(preConsume.getStatus() != 0){
			throw new ServiceException("该支付交易已处理过,请重新申请支付订单号");
		}
		return preConsume;
	}
	
	private Consume getConsume(String orderNo, String payType) {
		QueryWrapper<Consume> consumeQuery = new QueryWrapper<Consume>();
		consumeQuery.eq("order_no", orderNo);
		consumeQuery.eq("pay_type", payType);
		Consume consume = consumeService.getOne(consumeQuery);
		if(consume.getStatus() != 0) {
			log.info("[获取支付详情]: 订单已支付或支付失败, 订单号:{}, 支付方式:{}", orderNo, payType);
			throw new ServiceException("订单已支付或支付失败");
		}
		return consume;
	}
	
	private List<Consume> getConsumeList(String orderNo, String payType) {
		QueryWrapper<Consume> consumeQuery = new QueryWrapper<Consume>();
		consumeQuery.eq("order_no", orderNo);
		consumeQuery.eq("pay_type", payType);
		List<Consume> consumes = consumeService.list(consumeQuery);
		for(Consume consume : consumes) {
			if(consume.getStatus() != 0) {
				log.info("[获取支付详情列表]: 订单已支付或支付失败, 订单号:{}, 支付方式:{}", orderNo, consume.getPayType());
				throw new ServiceException("订单已支付或支付失败");
			}
		}
		return consumes;
	}

}
