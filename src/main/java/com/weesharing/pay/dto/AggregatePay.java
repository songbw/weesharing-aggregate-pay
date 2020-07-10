package com.weesharing.pay.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;

import com.weesharing.pay.dto.pay.*;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AggregatePay {

	@ApiModelProperty(value = "支付订单号")
	@NotBlank(message = "支付订单号不能为空")
	private String orderNo;

	@ApiModelProperty(value = "平台ID")
    @NotBlank(message = "AppId不能为空")
    private String appId;

	//余额支付
	private BalancePay balancePay;

	//联机账户支付
	private WOAPay woaPay;

	//惠民优选卡支付
	private List<WOCPay> wocPays;

	//快捷支付
	private BankPay bankPay;

	//凤巢支付宝
	private FcAlipayPay fcAlipayPay;

	//凤巢支付宝JSSDK支付
	private FcAliPayJsSdkPay fcAliPayJsSdkPay;

	//凤巢微信公众号
	private FcWxPay fcWxPay;

	//凤巢微信H5
	private FcWxH5Pay fcWxH5Pay;

	//凤巢微信小程序
	private FcWxXcxPay fcWxXcxPay;

	//平安统一支付
	private PingAnPay pingAnPay;

	//云城支付
	private YunChengPay yunChengPay;

	//云城支付
	private HuiYuPay huiYuPay;

}
