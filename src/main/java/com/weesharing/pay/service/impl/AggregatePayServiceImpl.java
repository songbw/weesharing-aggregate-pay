package com.weesharing.pay.service.impl;

import java.time.LocalDateTime;
import java.util.*;

import com.weesharing.pay.entity.PreRefund;
import com.weesharing.pay.service.*;
import com.weesharing.pay.utils.AggPayTradeDate;
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
import com.weesharing.pay.dto.PrePay;
import com.weesharing.pay.dto.PrePayResult;
import com.weesharing.pay.dto.pay.BankAuthBean;
import com.weesharing.pay.dto.query.QueryConsumeRefundRequest;
import com.weesharing.pay.dto.query.QueryConsumeRefundResult;
import com.weesharing.pay.dto.query.QueryConsumeResult;
import com.weesharing.pay.dto.query.QueryRefundResult;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.entity.PreConsume;
import com.weesharing.pay.entity.Refund;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.feign.FastBankPayService;
import com.weesharing.pay.feign.param.BankAuthBeanData;
import com.weesharing.pay.feign.result.BankAuthResult;
import com.weesharing.pay.service.handler.PayHandler;
import com.weesharing.pay.service.handler.RefundHandler;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
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
	private FastBankPayService fastBankPayService;

	@Autowired
	private RedisService redisService;

	@Autowired
	private PayHandler payHandler;

	@Autowired
	private RefundHandler refundHandler;

	@Autowired
	private IPreRefundService preRefundService;

	@Override
	public void
	normalizePreRefund(){
		log.info("normalize preRefund begin");
		int pageIndex = 1;
		int pageSize = 100;
		int totalUpdated = 0;
		int totalScaned = 0;

		while(0 != pageIndex) {
			QueryWrapper<PreRefund> wrapper = new QueryWrapper<>();
			wrapper.ne("trade_date","");
			IPage<PreRefund> queryPage = preRefundService.page(new Page<>(pageIndex, pageSize), wrapper);
			List<PreRefund> list = queryPage.getRecords();
			if(null == list || 0 == list.size()){
				break;
			}

			for(PreRefund record: list){
				PreRefund updateRecord = new PreRefund();
				updateRecord.setId(record.getId());
				String tradeDate = record.getTradeDate();
				if(AggPayTradeDate.needNormalize(tradeDate)){
					updateRecord.setTradeDate(AggPayTradeDate.buildTradeDate(tradeDate));
					updateRecord.updateById();
					totalUpdated++;
				}
			}
			totalScaned += list.size();
			log.info("normalize refund page:{} {}/{}",String.valueOf(pageIndex),String.valueOf(totalUpdated), String.valueOf(queryPage.getTotal()));
			if(totalScaned >= queryPage.getTotal()){
				break;
			}
			pageIndex++;

		}
		log.info("normalize preRefund end");

	}

	@Override
	public void
	normalizeRefund(){
		log.info("normalize refund begin");
		int pageIndex = 1;
		int pageSize = 100;
		int totalUpdated = 0;
		int totalScaned = 0;

		while(0 != pageIndex) {
			QueryWrapper<Refund> wrapper = new QueryWrapper<>();
			wrapper.ne("trade_date","");
			IPage<Refund> queryPage = refundService.page(new Page<>(pageIndex, pageSize), wrapper);
			List<Refund> list = queryPage.getRecords();
			if(null == list || 0 == list.size()){
				break;
			}

			for(Refund record: list){
				Refund updateRecord = new Refund();
				updateRecord.setId(record.getId());
				String tradeDate = record.getTradeDate();
				if(AggPayTradeDate.needNormalize(tradeDate)){
					updateRecord.setTradeDate(AggPayTradeDate.buildTradeDate(tradeDate));
					updateRecord.updateById();
					totalUpdated++;
				}
			}
			totalScaned += list.size();
			log.info("normalize refund page:{} {}/{}",String.valueOf(pageIndex),String.valueOf(totalUpdated), String.valueOf(queryPage.getTotal()));
			if(totalScaned >= queryPage.getTotal()){
				break;
			}
			pageIndex++;

		}
		log.info("normalize refund end");

	}

	@Override
	public void
	normalizeConsume(){
		log.info("normalize Consume begin");
		int pageIndex = 1;
		int pageSize = 100;
		int totalUpdated = 0;
		int totalScaned = 0;

		while(0 != pageIndex) {
			QueryWrapper<Consume> wrapper = new QueryWrapper<>();
			wrapper.ne("trade_date","");
			IPage<Consume> queryPage = consumeService.page(new Page<>(pageIndex, pageSize), wrapper);
			List<Consume> list = queryPage.getRecords();
			if(null == list || 0 == list.size()){
				break;
			}

			for(Consume record: list){
				Consume updateRecord = new Consume();
				updateRecord.setId(record.getId());
				String tradeDate = record.getTradeDate();
				if(AggPayTradeDate.needNormalize(tradeDate)){
					updateRecord.setTradeDate(AggPayTradeDate.buildTradeDate(tradeDate));
					///LocalDateTime createDate = record.getCreateDate();
					///updateRecord.setTradeDate(AggPayTradeDate.buildTradeDate(AggPayTradeDate.LocalDate2String(createDate.plusSeconds(30))));
					updateRecord.updateById();
					totalUpdated++;
				}
			}
			totalScaned += list.size();
			log.info("normalize Consume page:{} {}/{}",String.valueOf(pageIndex),String.valueOf(totalUpdated), String.valueOf(queryPage.getTotal()));
			if(totalScaned >= queryPage.getTotal()){
				break;
			}
			pageIndex++;

		}
		log.info("normalize Consume end");

	}

	@Override
	public void
	normalizePreRefundById(Long id) {
		log.info("normalize preRefund id={} begin", id);
		PreRefund record = preRefundService.getById(id);
		if (null == record) {
			log.info("没有 id={} 的记录", id);
			return;
		}

		PreRefund updateRecord = new PreRefund();
		updateRecord.setId(record.getId());
		String tradeDate = record.getTradeDate();
		if (AggPayTradeDate.needNormalize(tradeDate)) {
			String updateTradeDate = AggPayTradeDate.buildTradeDate(tradeDate);
			updateRecord.setTradeDate(updateTradeDate);
			updateRecord.updateById();
			log.info("normalize preRefund id={} from {} to {}", id,tradeDate,updateTradeDate);
		}

		log.info("normalize preRefund id={} end", id);

	}

	@Override
	public void
	normalizeRefundById(Long id) {
		log.info("normalize Refund id={} begin", id);
		Refund record = refundService.getById(id);
		if (null == record) {
			log.info("没有 id={} 的记录", id);
			return;
		}

		Refund updateRecord = new Refund();
		updateRecord.setId(record.getId());
		String tradeDate = record.getTradeDate();
		if (AggPayTradeDate.needNormalize(tradeDate)) {
			String updateTradeDate = AggPayTradeDate.buildTradeDate(tradeDate);
			updateRecord.setTradeDate(updateTradeDate);
			updateRecord.updateById();
			log.info("normalize Refund id={} from {} to {}", id,tradeDate,updateTradeDate);
		}

		log.info("normalize Refund id={} end", id);

	}

	@Override
	public void
	normalizeConsumeById(Long id) {
		log.info("normalize Consume id={} begin", id);
		Consume record = consumeService.getById(id);
		if (null == record) {
			log.info("没有 id={} 的记录", id);
			return;
		}

		Consume updateRecord = new Consume();
		updateRecord.setId(record.getId());
		String tradeDate = record.getTradeDate();
		if (AggPayTradeDate.needNormalize(tradeDate)) {
			String updateTradeDate = AggPayTradeDate.buildTradeDate(tradeDate);
			updateRecord.setTradeDate(updateTradeDate);
			updateRecord.updateById();
			log.info("normalize Consume id={} from {} to {}", id,tradeDate,updateTradeDate);
		}

		log.info("normalize Consumeid={} end", id);

	}

	/**
	 * 	申请预支付号
	 * 1. 查询最后一个预支付记录是否存在
	 * 2. 检查该订单是否已支付
	 * 2. 如果存在:
	 * 		检查是否过期[30分钟]
	 * 			   未过期:  直接返回预支付号
	 * 			   已过期:  生成新的预支付号
	 * 3.如果不存在:
	 * 		生成新的预支付号
	 */
	@Override
	public PrePayResult prePay(PrePay prePay) {

		QueryWrapper<PreConsume> preConsumeQuery = new QueryWrapper<PreConsume>();
		preConsumeQuery.eq("out_trade_no", prePay.getOutTradeNo());
		preConsumeQuery.eq("act_pay_fee", prePay.getActPayFee());
		preConsumeQuery.eq("status", 1);
		PreConsume success = preConsumeService.getOne(preConsumeQuery);
		if(success != null) {
			log.debug("预支付订单号已支付, 预支付号:{}", success.getOrderNo());
			throw new ServiceException("该订单已支付");
		}

		PreConsume preConsume = new PreConsume();
		String orderNo = UUID.randomUUID().toString().replaceAll("-", "");
		preConsume  = prePay.convert();
		preConsume.setOrderNo(orderNo);
		preConsume.insert();
		log.debug("预支付订单号生成完成, 预支付号: {}", orderNo);
		return new PrePayResult(orderNo, prePay.getOutTradeNo());

//		QueryWrapper<PreConsume> preConsumeQuery1 = new QueryWrapper<PreConsume>();
//		preConsumeQuery1.eq("out_trade_no", prePay.getOutTradeNo());
//		preConsumeQuery1.eq("act_pay_fee", prePay.getActPayFee());
//		preConsumeQuery1.eq("status", 0);
//		preConsumeQuery1.orderByDesc("create_date");
//		PreConsume preConsume = preConsumeService.getOne(preConsumeQuery1, false);
//		if(preConsume != null) {
//			if(isExpire(preConsume.getCreateDate())) {
//				String orderNo = UUID.randomUUID().toString().replaceAll("-", "");
//				preConsume  = prePay.convert();
//				preConsume.setOrderNo(orderNo);
//				preConsume.insert();
//				log.debug("预支付订单号已过期, 新的预支付号:{}", orderNo);
//				return new PrePayResult(orderNo, prePay.getOutTradeNo());
//			}
//			log.debug("预支付订单号已存在, 预支付号: {}", preConsume.getOrderNo());
//			return new PrePayResult(preConsume.getOrderNo(), prePay.getOutTradeNo());
//		}else {
//			String orderNo = UUID.randomUUID().toString().replaceAll("-", "");
//			preConsume  = prePay.convert();
//			preConsume.setOrderNo(orderNo);
//			preConsume.insert();
//			log.debug("预支付订单号生成完成, 预支付号: {}", orderNo);
//			return new PrePayResult(orderNo, prePay.getOutTradeNo());
//		}
	}

	@Override
	public Integer doPreQuery(String orderNo) {
		QueryWrapper<PreConsume> consumeQuery = new QueryWrapper<PreConsume>();
		consumeQuery.eq("order_no", orderNo);
		PreConsume consume = preConsumeService.getOne(consumeQuery);
		return consume.getStatus();
	}

	/**
	 * 	聚合支付
	 *
	 *  1. 检查预支付信息是否存在
	 * 	1. 与预支付信息进行对比总金额是否正确
	 *  2. 根据支付信息进行扣款处理
	 *  3. 结果状态处理
	 *     1: 成功
	 *     2: 失败  ==> 发起退款流程
	 *     3: 失败(超时)  超时情况默认失败 ==> 发起退款流程
	 */
	@Override
	public String doPay(AggregatePay pay) {
		return payHandler.doPay(pay);
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


	/**
	 * 	聚合自动退款
	 */
	@Override
	public String doRefund(AggregateRefund refund) {
		return refundHandler.doRefund(refund);
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
			results.add(new QueryRefundResult(refund));
		}
		return results;
	}

	@Override
	public CommonPage<QueryConsumeRefundResult> doQueryConsumeRefund(QueryConsumeRefundRequest request) {

		checkQueryConsumeAndRefundParam(request);

		if(request.getTradeType().equals("refund")) {

			QueryWrapper<Refund> wrapper = comboQueryWrapper(new QueryWrapper<Refund>(), request);
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

			QueryWrapper<Consume> wrapper = comboQueryWrapper(new QueryWrapper<Consume>(), request);
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

	private <T> QueryWrapper<T> comboQueryWrapper(QueryWrapper<T> wrapper, QueryConsumeRefundRequest request){
		if(StringUtils.isNotEmpty(request.getPayType())) {
			wrapper.eq("pay_type", request.getPayType());
		}
		if(StringUtils.isNotEmpty(request.getOrderNo())) {
			wrapper.eq("order_no", request.getOrderNo());
		}
		if(StringUtils.isNotEmpty(request.getCardNo())) {
			wrapper.eq("card_no", request.getCardNo());
		}
		if(StringUtils.isNotEmpty(request.getAppId())) {
			wrapper.eq("app_id", request.getAppId());
		}

		if(StringUtils.isNotEmpty(request.getStartDate())  &&  StringUtils.isNotEmpty(request.getEndDate())) {
			wrapper.ge("trade_date", AggPayTradeDate.buildTradeDate(request.getStartDate()));
			wrapper.lt("trade_date", AggPayTradeDate.buildTradeDate(request.getEndDate()));
		}
		wrapper.in("status", 1,3);
		return wrapper;
	}

	@Override
	public String fastPayAuth(BankAuthBean auth) {
		CommonResult2<BankAuthResult> result = fastBankPayService.bankAuth(new BankAuthBeanData(auth));
		log.info("快捷支付鉴权结果:{}", JSONUtil.wrap(result, false));
		if(result.getCode() == 200) {
			redisService.set("bank_auth:" + auth.getOrderNo(), JSONUtil.wrap(result.getData(), false).toString());
			return "快捷支付鉴权成功";
		}else {
			throw new ServiceException(result.getMsg());
		}
	}

	/**
	 * 检查消费记录参数
	 * @param request
	 */
	private void checkQueryConsumeAndRefundParam(QueryConsumeRefundRequest request) {
		if(request.getPageNum() < 1 || request.getPageSize() < 1) {
			throw new ServiceException("请检查页码");
		}
		if(request.getPageSize() > 100) {
			throw new ServiceException("请检查翻页参数, 太大翻不动.");
		}
		if(StringUtils.isNotEmpty(request.getStartDate())  &&  StringUtils.isNotEmpty(request.getEndDate())) {
			if(DateUtil.between(DateUtil.parse(request.getStartDate()), DateUtil.parse(request.getEndDate()), DateUnit.DAY) > 100) {
				throw new ServiceException("请调整日期, 时间间隔太大, 切记小于100天");
			}
		}
	}

	/**
	 * 交易是否过期
	 * @param createDate
	 * @return
	 */
//	private boolean isExpire(LocalDateTime createDate) {
//		Date db = Date.from(createDate.atZone(ZoneId.systemDefault()).toInstant());
//		if(db.before(DateUtil.offsetMinute(new Date(), -30))) {
//			return true;
//		}
//		return false;
//	}



}
