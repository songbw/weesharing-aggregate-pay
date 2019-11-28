package com.weesharing.pay.dto.query;

import java.time.LocalDateTime;

import com.weesharing.pay.dto.paytype.PayType;
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
public class QueryRefundResult {
	
	@ApiModelProperty(value = "支付方式")
	private String payType;

    @ApiModelProperty(value = "退款号")
    private String outRefundNo;

    @ApiModelProperty(value = "原订单号")
    private String sourceOutTradeNo;

    @ApiModelProperty(value = "支付订单号")
    private String orderNo;
    
    @ApiModelProperty(value = "退款号")
    private String refundNo;

    @ApiModelProperty(value = "商户编号")
    private String merchantCode;

    @ApiModelProperty(value = "交易总金额")
    private String totalFee;

    @ApiModelProperty(value = "交易实际金额")
    private String refundFee;

    @ApiModelProperty(value = "交易状态: 1: 成功, 2: 失败, 0: 新创建")
    private Integer status;
    
    @ApiModelProperty(value = "交易状态解释")
    private String statusMsg;

    @ApiModelProperty(value = "退款时间")
    private String tradeDate;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createDate;
	
	public QueryRefundResult(Refund refund, String bankStatusResult) {
		this.payType          = refund.getPayType();
		this.outRefundNo      = refund.getOutRefundNo();
		this.sourceOutTradeNo = refund.getSourceOutTradeNo();
		this.orderNo          = refund.getOrderNo();
		this.refundNo		  = refund.getRefundNo();
		this.merchantCode     = refund.getMerchantCode();
		this.totalFee         = refund.getTotalFee();
		this.refundFee        = refund.getRefundFee();
		this.status           = refund.getStatus();
		this.tradeDate        = refund.getTradeDate();
		this.createDate       = refund.getCreateDate();
		
		if(refund.getPayType().equals(PayType.BANK.getName())) {
			this.statusMsg = bankStatusResult;
		}else {
			if(status == 0) {
				this.statusMsg = "未处理";
			}else if(status == 1) {
				this.statusMsg = "退款成功";
			}else if(status == 2) {
				this.statusMsg = "退款失败";
			}else {
				this.statusMsg = "退款超时或异常";
			}
		}
	}

}
