package com.weesharing.pay.service.handler;

import java.util.Date;
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
		String pay_process = redisService.get("pay_process:" + pay.getOrderNo());
		if(StringUtils.isNotEmpty(pay_process)) {
			throw new ServiceException("该支付交易正在处理中, 请稍等.");
		}
		QueryWrapper<PreConsume> preConsumeQuery = new QueryWrapper<PreConsume>();
		preConsumeQuery.eq("order_no", pay.getOrderNo());
		PreConsume preConsume = preConsumeService.getOne(preConsumeQuery, false);
		if(preConsume == null) {
			throw new ServiceException("请核实支付订单号和支付金额再支付或者重新获取支付订单号");
		}else if(preConsume.getStatus() != 0){
			throw new ServiceException("该支付交易已处理过,请重新申请支付订单号");
		}
		//设置支付订单为处理中的状态
		redisService.set("pay_process:" + pay.getOrderNo(), pay.getOrderNo(), 30);
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
			
			if(pay.getBankPay() != null) {
				preActPayFee  = preActPayFee - Integer.parseInt(pay.getBankPay().getActPayFee());
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
						
						if(pay.getBankPay() != null) {
							redisService.set("bank_pay:" + pay.getOrderNo(), JSONUtil.wrap(pay.getBankPay(), false).toString());;
							consumeService.doPay(pay.getBankPay().convert());
						}
						preConsume.setStatus(1);
						log.info("聚合支付成功");
					}catch(Exception e) {
						e.printStackTrace();
						log.error("支付失败: {}, 参数: {}", e.getMessage(), JSONUtil.wrap(pay.getWoaPay(), false).toString());
						preConsume.setStatus(2);
						preConsume.insertOrUpdate();
						log.info("[支付失败] *** 开始回退余额支付的金额 *** ");
						refundHandler.doRefund(new AggregateRefund(preConsume));
						throw new ServiceException("支付失败:" + e.getMessage()) ;
					}
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

}
