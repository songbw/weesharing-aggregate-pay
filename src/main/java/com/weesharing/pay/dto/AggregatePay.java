package com.weesharing.pay.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;

import com.weesharing.pay.dto.pay.BalancePay;
import com.weesharing.pay.dto.pay.WOAPay;
import com.weesharing.pay.dto.pay.WOCPay;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AggregatePay {
	
	@ApiModelProperty(value = "支付订单号")
	@NotBlank(message = "支付订单号不能为空")
	private String orderNo;
	
	//余额支付
	private BalancePay balancePay;
	
	//联机账户支付
	private WOAPay woaPay;
	
	//惠民优选卡支付
	private List<WOCPay> wocPays;

}
