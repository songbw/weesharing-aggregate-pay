package com.weesharing.pay.dto;

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
    private String outRefundNo;

    @ApiModelProperty(value = "原订单号")
    private String sourceOutTradeNo;

    @ApiModelProperty(value = "支付订单号")
    private String orderNo;

    @ApiModelProperty(value = "商户编号")
    private String merchantCode;

    @ApiModelProperty(value = "交易总金额")
    private String totalFee;

    @ApiModelProperty(value = "交易实际金额")
    private String refundFee;

    @ApiModelProperty(value = "前端返回地址")
    private String returnUrl;

    @ApiModelProperty(value = "异步通知地址")
    private String notifyUrl;
    
    public Refund convert() {
    	Refund refund = new Refund();
    	refund.setOutRefundNo(this.getOutRefundNo());    
    	refund.setSourceOutTradeNo(this.getSourceOutTradeNo());
        refund.setOrderNo(this.getOrderNo());         
        refund.setMerchantCode(this.getMerchantCode());    
        refund.setTotalFee(this.getTotalFee());        
        refund.setRefundFee(this.getRefundFee());       
        refund.setReturnUrl(this.getReturnUrl());       
        refund.setNotifyUrl(this.getNotifyUrl());   
        return refund;
    }
}