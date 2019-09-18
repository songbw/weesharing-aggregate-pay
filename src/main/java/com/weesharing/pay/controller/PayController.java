package com.weesharing.pay.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weesharing.pay.common.CommonResult;
import com.weesharing.pay.dto.ConsumeResultDTO;
import com.weesharing.pay.dto.PayDTO;
import com.weesharing.pay.dto.PrePayDTO;
import com.weesharing.pay.dto.PrePayResultDTO;
import com.weesharing.pay.dto.RefundDTO;
import com.weesharing.pay.dto.RefundResultDTO;
import com.weesharing.pay.service.PayService;

@RestController
@RequestMapping("/wspay")
public class PayController {
	
	@Autowired
	private PayService payService;
	
	@PostMapping("/prepay")
	public CommonResult<PrePayResultDTO> prePay(@RequestBody PrePayDTO prePay){
		PrePayResultDTO payResult = payService.prePay(prePay);
		return CommonResult.success(payResult);
	}
	
	@PostMapping("/pay")
	public void pay(@RequestBody PayDTO pay, HttpServletResponse response) throws IOException{
		String returnUrl = payService.doPay(pay);
		response.sendRedirect(returnUrl);
	}
	
	@GetMapping("/query/pay")
	public CommonResult<ConsumeResultDTO> queryPay(String outTradeNo){
		ConsumeResultDTO consumeResult = payService.doQuery(outTradeNo);
		return CommonResult.success(consumeResult);
	}
	
	@PostMapping("/refund")
	public CommonResult<?> refund(@RequestBody RefundDTO refund){
		payService.doRefund(refund);
		return CommonResult.success();
	}
	
	@GetMapping("/query/refund")
	public CommonResult<RefundResultDTO> refund(String outTradeNo){
		RefundResultDTO refundResult = payService.doRefundQuery(outTradeNo);
		return CommonResult.success(refundResult);
	}

}
