package com.weesharing.pay.feign.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ZTKXCAS008ResponseBean {
	
	@ApiModelProperty(value="原交易流水号", example="CF300004853620191012215440842650")
    private String oriTranFlow;

    @ApiModelProperty(value="原交易状态: 00: 接口调用失败, 01 交易成功或者退款申请成功, 02 交易处理中,03 交易失败, 40: 退款成功, 41: 退款失败, ", example="01")
    private String oriTranStatus;

    @ApiModelProperty(value="原交易响应码", example="C000000000")
    private String oriRespCode;

    @ApiModelProperty(value="原交易响应信息", example="交易成功")
    private String oriRespMag;

    @ApiModelProperty(value="手续费", example="0.01")
    private String feeAmount;

    @ApiModelProperty(value="有效结算金额", example="0.01")
    private String settleAmount;

    @ApiModelProperty(value="交易完成时间,yyyyMMddHHmmssSSS", example="20191018140320742")
    private String finishTime;

}
