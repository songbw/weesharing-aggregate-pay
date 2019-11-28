package com.weesharing.pay.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weesharing.pay.common.CommonPage;
import com.weesharing.pay.common.CommonResult;
import com.weesharing.pay.dto.AggregatePay;
import com.weesharing.pay.dto.AggregateRefund;
import com.weesharing.pay.dto.PrePay;
import com.weesharing.pay.dto.PrePayResult;
import com.weesharing.pay.dto.pay.BankAuthBean;
import com.weesharing.pay.dto.query.QueryConsumeRefundRequest;
import com.weesharing.pay.dto.query.QueryConsumeRefundResult;
import com.weesharing.pay.dto.query.QueryConsumeResult;
import com.weesharing.pay.dto.query.QueryRefundResult;
import com.weesharing.pay.service.AggregatePayService;

import cn.hutool.json.JSONUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/wspay")
public class PayController {
	
	@Autowired
	private AggregatePayService payService;
	
	@PostMapping("/prepay")
	@ApiOperation(value="申请预支付号")
	public CommonResult<PrePayResult> prePay(@RequestBody @Valid PrePay prePay){
		PrePayResult payResult = payService.prePay(prePay);
		return CommonResult.success(payResult);
	}
	
	@GetMapping("/query/prepay")
	@ApiOperation(value="查询预支付结果状态", notes = "0: 未处理, 1: 成功, 2: 失败, 3: 超时, 4: 支付中")
	public CommonResult<Integer> queryPrePay(String orderNo){
		Integer consumeResult = payService.doPreQuery(orderNo);
		return CommonResult.success(consumeResult);
	}
	
	@PostMapping("/pay")
	@ApiOperation(value="支付", notes = "余额: balance, 惠民卡: card, 联机账户: woa, 快捷支付: bank")
	public CommonResult<String> pay(@RequestBody @Valid AggregatePay pay) throws IOException{
		log.info("[支付参数]:{}", JSONUtil.wrap(pay, false).toString());
		String tradeNo = payService.doPay(pay);
		return CommonResult.success(tradeNo);
	}
	
	@PostMapping("/refund")
	@ApiOperation(value="退款")
	public CommonResult<String> refund(@RequestBody @Valid AggregateRefund refund){
		try {
			String refundNo = payService.doRefund(refund);
			return CommonResult.success(refundNo);
		}catch(Exception se) {
			return CommonResult.failed(se.getMessage());
		}
	}
	
	@GetMapping("/query/pay")
	@ApiOperation(value="查询支付")
	public CommonResult<List<QueryConsumeResult>> queryPay(String orderNo){
		List<QueryConsumeResult> consumeResults = payService.doQuery(orderNo);
		return CommonResult.success(consumeResults);
	}
	
	@GetMapping("/batch/query/pay")
	@ApiOperation(value="批量查询支付")
	public CommonResult<Map<String, List<QueryConsumeResult>>> batchQueryPay(String orderNo){
		Map<String, List<QueryConsumeResult>> consumeResults = payService.doBatchQueryPay(orderNo);
		return CommonResult.success(consumeResults);
	}
	
	
	@GetMapping("/query/refund")
	@ApiOperation(value="查询退款")
	public CommonResult<List<QueryRefundResult>> refundQuery(String outRefundNo){
		List<QueryRefundResult> refundResults = payService.doRefundQuery(outRefundNo);
		return CommonResult.success(refundResults);
	}
	
	@GetMapping("/batch/query/refund")
	@ApiOperation(value="批量查询退款")
	public CommonResult<Map<String, List<QueryRefundResult>>> batchQueryRefund(String outRefundNos){
		Map<String, List<QueryRefundResult>> refundResults = payService.doBatchQueryRefund(outRefundNos);
		return CommonResult.success(refundResults);
	}
	
	@PostMapping("/query/candr")
	@ApiOperation(value="查询消费记录", notes="candr = Consume and Refund")
	public CommonResult<CommonPage<QueryConsumeRefundResult>> getQueryConsumeRefund(@RequestBody QueryConsumeRefundRequest request){
		CommonPage<QueryConsumeRefundResult> result = payService.doQueryConsumeRefund(request);
		return CommonResult.success(result);
	}
	
	@PostMapping("/fast/bank/auth")
	@ApiOperation(value="快捷支付鉴权")
	public CommonResult<String> fastPayAuth(@RequestBody @Valid BankAuthBean authBean){
		log.info("[快捷支付鉴权参数]:{}", JSONUtil.wrap(authBean, false).toString());
		String result = payService.fastPayAuth(authBean);
		return CommonResult.success(result);
	}

}
