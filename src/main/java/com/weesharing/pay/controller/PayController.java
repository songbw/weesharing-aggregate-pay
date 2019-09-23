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
import com.weesharing.pay.dto.ConsumeResultDTO;
import com.weesharing.pay.dto.PayDTO;
import com.weesharing.pay.dto.PrePayDTO;
import com.weesharing.pay.dto.PrePayResultDTO;
import com.weesharing.pay.dto.RefundDTO;
import com.weesharing.pay.dto.RefundResultDTO;
import com.weesharing.pay.service.PayService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/wspay")
public class PayController {
	
	@Autowired
	private PayService payService;
	
	@PostMapping("/prepay")
	@ApiOperation(value="申请预支付号")
	public CommonResult<PrePayResultDTO> prePay(@RequestBody @Valid PrePayDTO prePay){
		PrePayResultDTO payResult = payService.prePay(prePay);
		return CommonResult.success(payResult);
	}
	
	@PostMapping("/pay")
	@ApiOperation(value="支付")
	public CommonResult<String> pay(@RequestBody  @Valid PayDTO pay) throws IOException{
		String tradeNo = payService.doPay(pay);
		return CommonResult.success(tradeNo);
	}
	
	@GetMapping("/query/pay")
	@ApiOperation(value="查询支付")
	public CommonResult<List<ConsumeResultDTO>> queryPay(String orderNo){
		List<ConsumeResultDTO> consumeResults = payService.doQuery(orderNo);
		return CommonResult.success(consumeResults);
	}
	
	@PostMapping("/refund")
	@ApiOperation(value="退款")
	public CommonResult<String> refund(@RequestBody  @Valid RefundDTO refund){
		try {
			String refundNo = payService.doRefund(refund);
			return CommonResult.success(refundNo);
		}catch(Exception se) {
			return CommonResult.failed(se.getMessage());
		}
	}
	
	@GetMapping("/query/refund")
	@ApiOperation(value="查询退款")
	public CommonResult<List<RefundResultDTO>> refund(String orderNo){
		List<RefundResultDTO> refundResults = payService.doRefundQuery(orderNo);
		return CommonResult.success(refundResults);
	}

}
