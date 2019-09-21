package com.weesharing.pay.dto;

import java.time.ZoneId;
import java.util.Date;

import javax.validation.constraints.NotBlank;

import com.weesharing.pay.entity.Refund;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 	退款对象
 * </p>
 *
 * @author ZhangPeng
 * @since 2019-09-18
 */
@Data
@ApiModel(value="退款对象", description="")
public class RefundDTO {

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
    
    public Refund convert() {
    	Refund refund = new Refund();
    	refund.setOutRefundNo(this.getOutRefundNo());    
    	refund.setOrderNo(this.getOrderNo());
        refund.setMerchantCode(this.getMerchantCode());    
        refund.setRefundFee(this.getRefundFee());       
        refund.setReturnUrl(this.getReturnUrl());       
        refund.setNotifyUrl(this.getNotifyUrl()); 
        refund.setCreateDate(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        return refund;
    }
}