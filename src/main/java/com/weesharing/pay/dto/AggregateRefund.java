package com.weesharing.pay.dto;

import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import javax.validation.constraints.NotBlank;

import com.weesharing.pay.dto.pay.BalancePay;
import com.weesharing.pay.dto.pay.WOAPay;
import com.weesharing.pay.dto.pay.WOCPay;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.entity.PreRefund;
import com.weesharing.pay.entity.Refund;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * 退款方式用户无法选择由系统自动判断
 * 退款优先级: 余额 > 惠民优选卡 > 联机账户 > 其他
 * @author zp
 *
 */
@Data
public class AggregateRefund {

	@ApiModelProperty(value = "退款号")
    @NotBlank(message = "退款号不能为空")
    private String outRefundNo;

    @ApiModelProperty(value = "支付订单号")
    @NotBlank(message = "支付订单号不能为空")
	private String orderNo;

    @ApiModelProperty(value = "商户编号")
    private String merchantCode;

    @ApiModelProperty(value = "交易实际金额")
    @NotBlank(message = "交易实际金额不能为空")
    private String refundFee;

    @ApiModelProperty(value = "前端返回地址")
    private String returnUrl;

    @ApiModelProperty(value = "异步通知地址")
    private String notifyUrl;
    
    public AggregateRefund() {}

	public AggregateRefund(WOAPay woaPay) {
		this.outRefundNo = UUID.randomUUID().toString();
		this.orderNo = woaPay.getOrderNo();
		this.merchantCode = "";
		this.refundFee = woaPay.getActPayFee();
		this.returnUrl = "";
		this.notifyUrl = "";
	}

	public AggregateRefund(WOCPay wocPay) {
		this.outRefundNo = UUID.randomUUID().toString();
		this.orderNo = wocPay.getOrderNo();
		this.merchantCode = "";
		this.refundFee = wocPay.getActPayFee();
		this.returnUrl = "";
		this.notifyUrl = "";
	}

	public AggregateRefund(BalancePay balancePay) {
		this.outRefundNo = UUID.randomUUID().toString();
		this.orderNo = balancePay.getOrderNo();
		this.merchantCode = "";
		this.refundFee = balancePay.getActPayFee();
		this.returnUrl = "";
		this.notifyUrl = "";
	}
    
    public PreRefund convert() {
    	PreRefund refund = new PreRefund();
    	refund.setOutRefundNo(this.getOutRefundNo());    
    	refund.setOrderNo(this.getOrderNo());
        refund.setMerchantCode(this.getMerchantCode());    
        refund.setRefundFee(this.getRefundFee());       
        refund.setReturnUrl(this.getReturnUrl());       
        refund.setNotifyUrl(this.getNotifyUrl()); 
        refund.setCreateDate(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        return refund;
    }
    
    public Refund conver(PreRefund preRefund, Consume consume) {
		Refund refund = new Refund();
		refund.setOutRefundNo(preRefund.getOutRefundNo());
		refund.setMerchantCode(preRefund.getMerchantCode());
		refund.setCreateDate(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
		refund.setReturnUrl(preRefund.getReturnUrl());
		refund.setNotifyUrl(preRefund.getNotifyUrl());
		
		refund.setPayType(consume.getPayType());
		refund.setSourceOutTradeNo(consume.getOutTradeNo());
		refund.setOrderNo(consume.getOrderNo());
		refund.setTradeNo(consume.getTradeNo());
		refund.setTotalFee(consume.getTotalFee());
		refund.setRefundFee(consume.getActPayFee());
		refund.setCardNo(consume.getCardNo());
		refund.setCardPwd(consume.getCardPwd());
		return refund;
	}
    
}
