package com.weesharing.pay.dto.query;

import java.time.LocalDateTime;

import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.entity.Refund;

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
public class QueryConsumeRefundResult {
	
	@ApiModelProperty(value = "支付方式")
	private String payType;
	
	@ApiModelProperty(value = "订单号", example = "w123456")
	private String outTradeNo;
	
	@ApiModelProperty(value = "支付人")
	private String payer;

    @ApiModelProperty(value = "支付订单号")
    private String orderNo;

    @ApiModelProperty(value = "卡号或OpenID")
    private String cardNo;
    
    @ApiModelProperty(value = "交易类型", example = "consume", notes = "consume, refund")
    private String tradeType;

    @ApiModelProperty(value = "交易实际金额")
    private String actPayFee;

    @ApiModelProperty(value = "交易状态", notes="1: 成功, 2: 失败, 3:超时")
    private Integer status;

    @ApiModelProperty(value = "交易时间")
    private String tradeDate;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createDate;

	public QueryConsumeRefundResult(Consume consume) {
		this.payType    = consume.getPayType();
		this.payer      = consume.getPayer();
		this.orderNo    = consume.getOrderNo();
		this.cardNo     = consume.getCardNo();
		this.tradeType  = "consume";
		this.actPayFee = consume.getActPayFee();
		this.status     = consume.getStatus();
		this.tradeDate  = consume.getTradeDate();
		this.createDate = consume.getCreateDate();
		this.outTradeNo = consume.getOutTradeNo();
	}
	
	public QueryConsumeRefundResult(Refund refund) {
		this.payType    = refund.getPayType();
		this.payer      = refund.getTradeNo();
		this.orderNo    = refund.getOrderNo();
		this.cardNo     = refund.getCardNo();
		this.tradeType  = "refund";
		this.outTradeNo = refund.getSourceOutTradeNo();
		this.actPayFee  = refund.getRefundFee();
		this.status     = refund.getStatus();
		this.tradeDate  = refund.getTradeDate();
		this.createDate = refund.getCreateDate();
	}
}
