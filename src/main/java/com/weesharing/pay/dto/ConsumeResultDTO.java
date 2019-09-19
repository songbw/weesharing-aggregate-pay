package com.weesharing.pay.dto;

import java.time.LocalDateTime;

import com.weesharing.pay.entity.Consume;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 	消费记录对象
 * </p>
 *
 * @author ZhangPeng
 * @since 2019-09-18
 */
@Data
@ApiModel(value="消费记录对象", description="")
public class ConsumeResultDTO {

    @ApiModelProperty(value = "支付订单号")
    private String orderNo;

    @ApiModelProperty(value = "订单号")
    private String outTradeNo;

    @ApiModelProperty(value = "联机账户订单号")
    private String tradeNo;

    @ApiModelProperty(value = "商品描述")
    private String body;

    @ApiModelProperty(value = "用户自定义")
    private String remark;

    @ApiModelProperty(value = "交易总金额")
    private String totalFee;

    @ApiModelProperty(value = "交易实际金额")
    private String actPayFee;

    @ApiModelProperty(value = "交易状态: 1: 成功, 2: 失败, 0: 新创建")
    private Integer status;

    @ApiModelProperty(value = "交易时间")
    private String tradeDate;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createDate;

    @ApiModelProperty(value = "支付限制")
    private String limitPay;

	public ConsumeResultDTO(Consume consume) {
		this.orderNo    = consume.getOrderNo();
		this.outTradeNo = consume.getOutTradeNo();
		this.tradeNo    = consume.getTradeNo();
		this.body       = consume.getBody();
		this.remark     = consume.getRemark();
		this.totalFee   = consume.getTotalFee();
		this.actPayFee = consume.getActPayFee();
		this.status     = consume.getStatus();
		this.tradeDate  = consume.getTradeDate();
		this.createDate = consume.getCreateDate();
		this.limitPay   = consume.getLimitPay();
	}

}
