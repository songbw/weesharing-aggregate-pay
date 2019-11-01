package com.weesharing.pay.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 	查询消费和退款对象
 * </p>
 *
 * @author ZhangPeng
 * @since 2019-09-18
 */
@Data
@ApiModel(value="查询消费和退款对象", description="")
public class QueryConsumeRefundRequest {
	
	@ApiModelProperty(value = "支付方式", example = "balance", notes="balance, card, woa")
	private String payType;

    @ApiModelProperty(value = "支付订单号", example = "")
    private String orderNo;
    
    @ApiModelProperty(value = "卡号或OpenID", example = "")
    private String cardNo;
    
    @ApiModelProperty(value = "交易类型", example = "consume", notes = "consume, refund")
    private String tradeType;

    @ApiModelProperty(value = "开始时间", example = "", notes="yyyyMMddHHmmss")
    private String startDate;
    
    @ApiModelProperty(value = "结束时间", example = "", notes="yyyyMMddHHmmss")
    private String endDate;
    
    private Long pageNum;
    
    private Long pageSize;

}
