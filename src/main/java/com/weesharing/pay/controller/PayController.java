package com.weesharing.pay.controller;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weesharing.pay.common.CommonResult;
import com.weesharing.pay.dto.AggregatePay;
import com.weesharing.pay.dto.AggregateRefund;
import com.weesharing.pay.dto.BankAuthBean;
import com.weesharing.pay.dto.PrePay;
import com.weesharing.pay.dto.PrePayResult;
import com.weesharing.pay.dto.QueryConsumeResult;
import com.weesharing.pay.dto.QueryRefundResult;
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
	
	@PostMapping("/fast/bank/auth")
	@ApiOperation(value="快捷支付鉴权")
	public CommonResult<String> fastPayAuth(@RequestBody @Valid BankAuthBean authBean){
		log.info("[快捷支付鉴权参数]:{}", JSONUtil.wrap(authBean, false).toString());
		String result = payService.fastPayAuth(authBean);
		return CommonResult.success(result);
	}
	
	@PostMapping("/pay")
	@ApiOperation(value="支付", notes = "余额: balance, 惠民卡: card, 联机账户: woa, 快捷支付: bank")
	public CommonResult<String> pay(@RequestBody @Valid AggregatePay pay) throws IOException{
		log.info("[支付参数]:{}", JSONUtil.wrap(pay, false).toString());
		String tradeNo = payService.doPay(pay);
		return CommonResult.success(tradeNo);
	}
	
	@GetMapping("/query/pay")
	@ApiOperation(value="查询支付")
	public CommonResult<List<QueryConsumeResult>> queryPay(String orderNo){
		List<QueryConsumeResult> consumeResults = payService.doQuery(orderNo);
		return CommonResult.success(consumeResults);
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
	
	@GetMapping("/query/refund")
	@ApiOperation(value="查询退款")
	public CommonResult<List<QueryRefundResult>> refundQuery(String orderNo){
		List<QueryRefundResult> refundResults = payService.doRefundQuery(orderNo);
		return CommonResult.success(refundResults);
	}

}
