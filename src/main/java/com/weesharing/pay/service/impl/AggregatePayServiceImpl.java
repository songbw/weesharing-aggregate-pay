package com.weesharing.pay.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.weesharing.pay.common.CommonPage;
import com.weesharing.pay.common.CommonResult2;
import com.weesharing.pay.dto.AggregatePay;
import com.weesharing.pay.dto.AggregateRefund;
import com.weesharing.pay.dto.BackBean;
import com.weesharing.pay.dto.BackRequest;
import com.weesharing.pay.dto.BankAuthBean;
import com.weesharing.pay.dto.PrePay;
import com.weesharing.pay.dto.PrePayResult;
import com.weesharing.pay.dto.QueryConsumeRefundRequest;
import com.weesharing.pay.dto.QueryConsumeRefundResult;
import com.weesharing.pay.dto.QueryConsumeResult;
import com.weesharing.pay.dto.QueryRefundResult;
import com.weesharing.pay.dto.RefundResult;
import com.weesharing.pay.dto.pay.PayType;
import com.weesharing.pay.dto.pay.WOCPay;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.entity.PreConsume;
import com.weesharing.pay.entity.PreRefund;
import com.weesharing.pay.entity.Refund;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.feign.BeanContext;
import com.weesharing.pay.feign.FastBankPayService;
import com.weesharing.pay.feign.WorkOrderService;
import com.weesharing.pay.feign.param.BankAuthBeanData;
import com.weesharing.pay.feign.result.BankAuthResult;
import com.weesharing.pay.service.AggregatePayService;
import com.weesharing.pay.service.IConsumeService;
import com.weesharing.pay.service.IPreConsumeService;
import com.weesharing.pay.service.IPreRefundService;
import com.weesharing.pay.service.IRefundService;
import com.weesharing.pay.service.RedisService;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service(value = "payService")
public class AggregatePayServiceImpl implements AggregatePayService{
	
	@Autowired
	private IConsumeService consumeService;
	
	@Autowired
	private IRefundService refundService;
	
	@Autowired
	private IPreConsumeService preConsumeService;
	
	@Autowired
	private IPreRefundService preRefundService;
	
	@Autowired
	private FastBankPayService fastBankPayService;
	
	@Autowired
	private RedisService redisService;
	
	private ExecutorService executor = Executors.newCachedThreadPool() ;
	
	@Override
	public PrePayResult prePay(PrePay prePay) {
		
		/**
		 * 1. ?????????????????????????????????????????????
		 * 2. ??????????????????????????????
		 * 2. ????????????:
		 * 		??????????????????[30??????]
		 * 			   ?????????:  ???????????????????????? 
		 * 			   ?????????:  ????????????????????????
		 * 3.???????????????: 
		 * 		????????????????????????
		 */
		QueryWrapper<PreConsume> preConsumeQuery = new QueryWrapper<PreConsume>();
		preConsumeQuery.eq("out_trade_no", prePay.getOutTradeNo());
		preConsumeQuery.eq("act_pay_fee", prePay.getActPayFee());
		preConsumeQuery.eq("status", 1);
		PreConsume success = preConsumeService.getOne(preConsumeQuery);
		if(success != null) {
			log.debug("???????????????????????????, ????????????:{}", success.getOrderNo());
			throw new ServiceException("??????????????????");
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
				log.debug("???????????????????????????, ??????????????????:{}", orderNo);
				return new PrePayResult(orderNo, prePay.getOutTradeNo());
			}
			log.debug("???????????????????????????, ????????????: {}", preConsume.getOrderNo());
			return new PrePayResult(preConsume.getOrderNo(), prePay.getOutTradeNo());
		}else {
			String orderNo = UUID.randomUUID().toString().replaceAll("-", "");
			preConsume  = prePay.convert();
			preConsume.setOrderNo(orderNo);
			preConsume.insert();
			log.debug("??????????????????????????????, ????????????: {}", orderNo);
			return new PrePayResult(orderNo, prePay.getOutTradeNo());
		}
	}
	
	@Override
	public String fastPayAuth(BankAuthBean auth) {
		CommonResult2<BankAuthResult> result = fastBankPayService.bankAuth(new BankAuthBeanData(auth));
		log.info("????????????????????????:{}", JSONUtil.wrap(result, false));
		if(result.getCode() == 200) {
			redisService.set("bank_auth:" + auth.getOrderNo(), JSONUtil.wrap(result.getData(), false).toString());
			return "????????????????????????";
		}else {
			throw new ServiceException(result.getMsg());
		}
	}

	@Override
	public String doPay(AggregatePay pay) {
		/**
		 *  1. ?????????????????????????????????
		 * 	1. ???????????????????????????????????????????????????
		 *  2. ????????????????????????????????????
		 *  3. ??????????????????
		 *     1: ??????
		 *     2: ??????  ==> ??????????????????
		 *     3: ??????(??????)  ???????????????????????? ==> ??????????????????
		 */
		String pay_process = redisService.get("pay_process:" + pay.getOrderNo());
		if(StringUtils.isNotEmpty(pay_process)) {
			throw new ServiceException("??????????????????????????????, ?????????.");
		}
		QueryWrapper<PreConsume> preConsumeQuery = new QueryWrapper<PreConsume>();
		preConsumeQuery.eq("order_no", pay.getOrderNo());
//		preConsumeQuery.eq("status", 0);
//		preConsumeQuery.orderByDesc("create_date");
		PreConsume preConsume = preConsumeService.getOne(preConsumeQuery, false);
		if(preConsume == null) {
			throw new ServiceException("?????????????????????????????????????????????????????????????????????????????????");
		}else if(preConsume.getStatus() != 0){
			throw new ServiceException("???????????????????????????,??????????????????????????????");
		}
		//???????????????????????????????????????
		redisService.set("pay_process:" + pay.getOrderNo(), pay.getOrderNo(), 30);
		//???????????????????????????
		if(checkPayFee(preConsume, pay) == 1) {
			//??????????????????????????????
			asyncPay(preConsume, pay, true);
		}else if(checkPayFee(preConsume, pay) == 0) {
			//??????????????????0???????????????
			asyncPay(preConsume, pay, false);
		}else {
			throw new ServiceException("????????????, ?????????????????????");
		}
		return preConsume.getTradeNo();
	}
	
	/**
	 * ????????????????????????????????????????????????
	 * @param preConsume
	 * @param pay
	 * @return 1: ??????, 0: ??????,??????0?????????, 2: ?????????
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
						log.info("??????????????????");
					}catch(Exception e) {
						e.printStackTrace();
						log.error("????????????: {}, ??????: {}", e.getMessage(), JSONUtil.wrap(pay.getWoaPay(), false).toString());
						preConsume.setStatus(2);
						preConsume.insertOrUpdate();
						log.info("[????????????] *** ????????????????????????????????? *** ");
						doRefund(new AggregateRefund(preConsume));
						throw new ServiceException("????????????:" + e.getMessage()) ;
					}
				}else {
					preConsume.setStatus(1);
					log.info("??????0???????????????");
				}
				preConsume.setTradeDate(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
				preConsume.insertOrUpdate();

				//??????
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
	public Integer doPreQuery(String orderNo) {
		QueryWrapper<PreConsume> consumeQuery = new QueryWrapper<PreConsume>();
		consumeQuery.eq("order_no", orderNo);
		PreConsume consume = preConsumeService.getOne(consumeQuery);
		return consume.getStatus();
	}
	
	@Override
	public Map<String, List<QueryConsumeResult>> doBatchQueryPay(String orderNo) {
		QueryWrapper<Consume> consumeQuery = new QueryWrapper<Consume>();
		consumeQuery.in("order_no", Arrays.asList(orderNo.split(",")));
		List<Consume> consumes = consumeService.list(consumeQuery);
		
		Map<String, List<QueryConsumeResult>> queryResults = new HashMap<String, List<QueryConsumeResult>>();
		for(String num : orderNo.split(",")) {
			List<QueryConsumeResult> results = new ArrayList<QueryConsumeResult>();
			for(Consume consume : consumes) {
				if(consume.getOrderNo().equals(num)) {
					results.add(new QueryConsumeResult(consume));
				}
			}
			queryResults.put(num, results);
		}
		
		return queryResults;
	}
	
	@Override
	public String doRefund(AggregateRefund refund) {
		QueryWrapper<PreConsume> preConsumeQuery = new QueryWrapper<PreConsume>();
		preConsumeQuery.eq("order_no", refund.getOrderNo());
//		preConsumeQuery.eq("status", 1);
		PreConsume preConsume = preConsumeService.getOne(preConsumeQuery);
		if(preConsume == null ) {
			throw new ServiceException("????????????????????????????????????,??????????????????.");
		}
		
		QueryWrapper<PreRefund> preRefundQuery = new QueryWrapper<PreRefund>();
		preRefundQuery.eq("out_refund_no", refund.getOutRefundNo());
		preRefundQuery.eq("order_no", refund.getOrderNo());
		
		PreRefund preRefund = preRefundService.getOne(preRefundQuery);
		if(preRefund != null ) {
			throw new ServiceException("??????????????????, ?????????????????????,???????????????????????????.");
		}
		
		preRefund = refund.convert();
		preRefund.setSourceOutTradeNo(preConsume.getOutTradeNo());
		preRefund.setTotalFee(refund.getRefundFee());
		preRefund.insert();
		
		//??????????????????
		if(checkRefundFee(preConsume, refund)) {
			//??????????????????????????????
			asyncRefund(preRefund, refund);
		}else {
			throw new ServiceException("????????????, ?????????????????????");
		}
		
		return preRefund.getOutRefundNo();
	}
	
	/**
	 * ??????????????????????????????????????????
	 * @param preConsume
	 * @param refund
	 * @return
	 */
	private boolean checkRefundFee(PreConsume preConsume, AggregateRefund refund) {
		/**
		 * 1. ?????????????????????????????????
		 * 2. ????????????????????????????????????
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
				PreRefund refundResult = autoAllocationRefund(preRefund, refund);
				//??????
				RefundResult result  = new RefundResult(refundResult);
				refundNotifyHandler(result);
			}});
	}
	
	/**
	 * ??????????????????????????????????????????
	 * @param preRefund
	 * @param refund
	 * @param refundTotal
	 * @return
	 */
	private PreRefund autoAllocationRefund(PreRefund preRefund, AggregateRefund aggregateRefund) {
		//1: ??????, 2: ??????, 3: ????????????, 0: ?????????
		Long refundTotal = Long.parseLong(aggregateRefund.getRefundFee());
		log.info("???????????????: {}", refundTotal);
		Long remainTotal = 0L;
		
		if (refundTotal > 0) {
			List<Consume> consumes = autoAllocationRefundHandler(preRefund, aggregateRefund, refundTotal);
			for (Consume refund : consumes) {
				try {
					remainTotal = remainTotal + Long.parseLong(refund.getActPayFee());
					refundService.doRefund(aggregateRefund.conver(preRefund, refund));
				} catch (Exception e) {
					log.info("????????????:{}", e.getMessage());
					remainTotal = remainTotal - Long.parseLong(refund.getActPayFee());
				}
			}
			
			//??????????????????
			if(remainTotal == 0) {
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
		
		return preRefund;
	}
	
	/**
	 * ???????????????????????????????????????
	 * @param preRefund
	 * @param refund
	 * @param refundTotal
	 * @param payType
	 * @return
	 */
	private List<Consume> autoAllocationRefundHandler(PreRefund preRefund, AggregateRefund refund, Long refundTotal) {
		
		String refundTypes[] = {PayType.BALANCE.getName(), PayType.CARD.getName(), PayType.WOA.getName(), PayType.BANK.getName()};
		List<Consume> refunds = new ArrayList<Consume>();
		
		for(String refundType: refundTypes) {
			if (refundTotal > 0) {
				QueryWrapper<Consume> consumeQuery = new QueryWrapper<Consume>();
				consumeQuery.eq("pay_type", refundType);
				consumeQuery.eq("order_no", refund.getOrderNo());
				consumeQuery.eq("status", 1);
				List<Consume> consumes = consumeService.list(consumeQuery);
				Long payTotal = consumes.stream().mapToLong(pay -> Long.parseLong(pay.getActPayFee())).sum();
				
				QueryWrapper<Refund> refundQuery = new QueryWrapper<Refund>();
				refundQuery.eq("pay_type", refundType);
				refundQuery.eq("order_no", refund.getOrderNo());
				refundQuery.eq("status", 1);
				List<Refund> refundeds = refundService.list(refundQuery);
				Long processTotal = refundeds.stream().mapToLong(refunded -> Long.parseLong(refunded.getRefundFee())).sum();
				
				//?????????????????????
				Long remainTotal = payTotal - processTotal;
				if(consumes != null && consumes.size() > 0 && refundTotal > 0 && remainTotal > 0) {
					
					log.info("[??????]{}?????????????????????: {}", refundType, remainTotal);
					
					for(Consume consume : consumes) {
						//???????????????????????????????????????
						Long remain = Long.parseLong(consume.getActPayFee()) ;
						for( Refund refunded:refundeds) {
							if(refunded.getCardNo().equals(consume.getCardNo())) {
								remain = remain - Long.parseLong(refunded.getRefundFee());
							}
						}
						
						if (refundTotal > 0 && remain > 0) {
							log.info("[??????] *** ??????????????????????????? *** ");
							
							if(refundTotal >= remain) {
								consume.setActPayFee(String.valueOf(remain));
								log.info("[??????] ??????: {}", remain);
								refundTotal = refundTotal - remain;
							}else {
								consume.setActPayFee(String.valueOf(refundTotal));
								log.info("[??????] ??????: {}", refundTotal);
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

	@Override
	public List<QueryRefundResult> doRefundQuery(String outRefundNo) {
		QueryWrapper<Refund> refundQuery = new QueryWrapper<Refund>();
		refundQuery.eq("out_refund_no", outRefundNo);
		List<Refund> refunds = refundService.list(refundQuery);
		return getQueryRefundResult(refunds);
	}
	
	@Override
	public Map<String, List<QueryRefundResult>> doBatchQueryRefund(String outRefundNos) {
		Map<String, List<QueryRefundResult>> queryResults = new HashMap<String, List<QueryRefundResult>>();
		for(String num : outRefundNos.split(",")) {
			QueryWrapper<Refund> refundQuery = new QueryWrapper<Refund>();
			refundQuery.eq("out_refund_no", num);
			List<Refund> refunds = refundService.list(refundQuery);
			queryResults.put(num, getQueryRefundResult(refunds));
		}
		return queryResults;
	}
	
	private List<QueryRefundResult> getQueryRefundResult(List<Refund> refunds){
		List<QueryRefundResult> results = new ArrayList<QueryRefundResult>();
		for(Refund refund: refunds) {
			String bankStatusResult = "??????????????????";
			if(refund.getPayType().equals(PayType.BANK.getName()) && StringUtils.isNotEmpty(refund.getRefundNo())) {
				CommonResult2<String> bankResult = fastBankPayService.refundStatus(refund.getRefundNo());
				log.info(JSONUtil.wrap(bankResult, false).toString());
				if(bankResult != null) {
					bankStatusResult = bankResult.getMsg();
				}
			}
			results.add(new QueryRefundResult(refund,  bankStatusResult));
		}
		return results;
	}
	
	@Override
	public CommonPage<QueryConsumeRefundResult> doQueryConsumeRefund(QueryConsumeRefundRequest request) {
		
		checkQueryConsumeAndRefundParam(request);
		
		if(request.getTradeType().equals("refund")) {
			
			QueryWrapper<Refund> wrapper = new QueryWrapper<Refund>();
			if(StringUtils.isNotEmpty(request.getPayType())) {
				wrapper.eq("pay_type", request.getPayType());
			}
			if(StringUtils.isNotEmpty(request.getOrderNo())) {
				wrapper.eq("order_no", request.getOrderNo());
			}
			if(StringUtils.isNotEmpty(request.getCardNo())) {
				wrapper.eq("card_no", request.getCardNo());
			}
			if(StringUtils.isNotEmpty(request.getStartDate())  &&  StringUtils.isNotEmpty(request.getEndDate())) {
				wrapper.ge("create_date", request.getStartDate());
				wrapper.lt("create_date", request.getEndDate());
			}
//			wrapper.gt("status", 1);
			wrapper.in("status", 1,3);
			
			IPage<Refund> refundPage = refundService.page(new Page<Refund>(request.getPageNum(), request.getPageSize()), wrapper);
			IPage<QueryConsumeRefundResult> results = new Page<QueryConsumeRefundResult>(refundPage.getCurrent(), refundPage.getSize(), refundPage.getTotal());
			List<QueryConsumeRefundResult> qcrr = new ArrayList<QueryConsumeRefundResult>();
			for(Refund refund : refundPage.getRecords()){
				qcrr.add(new QueryConsumeRefundResult(refund));
			};
			results.setRecords(qcrr);
			return CommonPage.restPage(results);
		}
		
		if(request.getTradeType().equals("consume")) {
			
			QueryWrapper<Consume> wrapper = new QueryWrapper<Consume>();
			if(StringUtils.isNotEmpty(request.getPayType())) {
				wrapper.eq("pay_type", request.getPayType());
			}
			if(StringUtils.isNotEmpty(request.getOrderNo())) {
				wrapper.eq("order_no", request.getOrderNo());
			}
			if(StringUtils.isNotEmpty(request.getCardNo())) {
				wrapper.eq("card_no", request.getCardNo());
			}
			if(StringUtils.isNotEmpty(request.getStartDate())  &&  StringUtils.isNotEmpty(request.getEndDate())) {
				wrapper.ge("create_date", request.getStartDate());
				wrapper.lt("create_date", request.getEndDate());
			}
//			wrapper.gt("status", 0);
			wrapper.in("status", 1,3);
			
			IPage<Consume> consumePage = consumeService.page(new Page<Consume>(request.getPageNum(), request.getPageSize()), wrapper);
			IPage<QueryConsumeRefundResult> results = new Page<QueryConsumeRefundResult>(consumePage.getCurrent(), consumePage.getSize(), consumePage.getTotal());
			List<QueryConsumeRefundResult> qcrr = new ArrayList<QueryConsumeRefundResult>();
			for(Consume consume : consumePage.getRecords()){
				qcrr.add(new QueryConsumeRefundResult(consume));
			};
			results.setRecords(qcrr);
			return CommonPage.restPage(results);
		}
		
		return null;
	}
	
	/**
	 * ????????????????????????
	 * @param request
	 */
	private void checkQueryConsumeAndRefundParam(QueryConsumeRefundRequest request) {
		if(request.getPageNum() < 1 || request.getPageSize() < 1) {
			throw new ServiceException("???????????????");
		}
		if(request.getPageSize() > 100) {
			throw new ServiceException("?????????????????????, ???????????????.");
		}
		if(StringUtils.isNotEmpty(request.getStartDate())  &&  StringUtils.isNotEmpty(request.getEndDate())) {
			if(DateUtil.between(DateUtil.parse(request.getStartDate()), DateUtil.parse(request.getEndDate()), DateUnit.DAY) > 100) {
				throw new ServiceException("???????????????, ??????????????????, ????????????100???");
			}
		}
	}
	

	/**
	 * ??????????????????
	 * @param notifyUrl
	 * @param json
	 */
	private void payNotifyHandler(String notifyUrl, String json) {
		log.info("????????????, ????????????...");
		log.info("????????????:{}, ??????: {}", notifyUrl, json);
		executor.submit(new Runnable(){
			@Override
			public void run() {
				HttpUtil.post(notifyUrl, json);
			}
		});
	}

	/**
	 * ??????????????????
	 * @param notifyUrl
	 * @param json
	 */
	@SuppressWarnings("unused")
	private void refundNotifyHandler(String notifyUrl, String json) {
		executor.submit(new Runnable(){
			@Override
			public void run() {
				log.debug("????????????, ??????????????????:{}, ??????: {}", notifyUrl, json);
				HttpUtil.post(notifyUrl, json);
			}
		});
	}
	
	private void refundNotifyHandler(RefundResult result) {
		executor.submit(new Runnable(){
			@Override
			public void run() {
				log.info("????????????, ??????: {}", JSONUtil.wrap(result, false).toString());
				BeanContext.getBean(WorkOrderService.class).refundNotify(result);
			}
		});
	}
	
	/**
	 * ??????????????????
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
