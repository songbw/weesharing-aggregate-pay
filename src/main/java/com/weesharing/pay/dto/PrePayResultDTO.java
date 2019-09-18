package com.weesharing.pay.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 预支付结果对象
 * </p>
 *
 * @author ZhangPeng
 * @since 2019-09-18
 */

@Data
@ApiModel(value = "预支付结果对象", description = "")
public class PrePayResultDTO {

	@ApiModelProperty(value = "支付订单号")
	private String orderNo;

	@ApiModelProperty(value = "订单号")
	private String outTradeNo;

	public PrePayResultDTO(String orderNo, String outTradeNo) {
		this.orderNo = orderNo;
		this.outTradeNo = outTradeNo;
	}

}
