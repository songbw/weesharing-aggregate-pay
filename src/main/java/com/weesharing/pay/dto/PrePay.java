package com.weesharing.pay.dto;

import java.time.ZoneId;
import java.util.Date;

import javax.validation.constraints.NotBlank;

import com.weesharing.pay.entity.PreConsume;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 	预支付对象
 * </p>
 *
 * @author ZhangPeng
 * @since 2019-09-18
 */
@Data
@ApiModel(value="预支付对象", description="")
public class PrePay {

    @ApiModelProperty(value = "订单号")
    @NotBlank(message = "订单号不能为空")
    private String outTradeNo;

    @ApiModelProperty(value = "商品描述")
    @NotBlank(message = "商品描述不能为空")
    private String body;

    @ApiModelProperty(value = "用户自定义")
    private String remark;

    @ApiModelProperty(value = "交易总金额")
    private String totalFee;

    @ApiModelProperty(value = "交易实际金额")
    @NotBlank(message = "交易实际金额不能为空")
    private String actPayFee;

    @ApiModelProperty(value = "支付限制")
    private String limitPay;

    @ApiModelProperty(value = "前端返回地址")
    private String returnUrl;

    @ApiModelProperty(value = "异步通知地址")
    @NotBlank(message = "异步通知地址不能为空")
    private String notifyUrl;
    
    public PreConsume convert() {
    	PreConsume preConsume = new PreConsume();
    	preConsume.setOutTradeNo(this.getOutTradeNo());
    	preConsume.setBody(this.getBody());
    	preConsume.setRemark(this.getRemark());
        preConsume.setTotalFee(this.getTotalFee());   
        preConsume.setActPayFee(this.getActPayFee());
        preConsume.setLimitPay(this.getLimitPay());
        preConsume.setReturnUrl(this.getReturnUrl());       
        preConsume.setNotifyUrl(this.getNotifyUrl());  
        preConsume.setCreateDate(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        return preConsume;
    }

}
