package com.weesharing.pay.controller;

import java.io.IOException;
import java.util.List;

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
	public CommonResult<List<ConsumeResultDTO>> queryPay(String outTradeNo){
		List<ConsumeResultDTO> consumeResults = payService.doQuery(outTradeNo);
		return CommonResult.success(consumeResults);
	}
	
	@PostMapping("/refund")
	public CommonResult<?> refund(@RequestBody RefundDTO refund){
		payService.doRefund(refund);
		return CommonResult.success();
	}
	
	@GetMapping("/query/refund")
	public CommonResult<List<RefundResultDTO>> refund(String outTradeNo){
		List<RefundResultDTO> refundResults = payService.doRefundQuery(outTradeNo);
		return CommonResult.success(refundResults);
	}

}
