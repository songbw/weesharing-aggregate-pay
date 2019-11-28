package com.weesharing.pay.dto.query;

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

    @ApiModelProperty(value = "开始时间", example = "2019-10-01 00:00:00", notes="yyyyMMddHHmmss")
    private String startDate;
    
    @ApiModelProperty(value = "结束时间", example = "2019-10-30 00:00:00", notes="yyyyMMddHHmmss")
    private String endDate;
    
    @ApiModelProperty(value = "页码", example = "1")
    private Long pageNum;
    
    @ApiModelProperty(value = "条数", example = "10")
    private Long pageSize;

}
