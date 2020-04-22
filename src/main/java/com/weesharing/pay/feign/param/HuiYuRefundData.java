package com.weesharing.pay.feign.param;

import com.weesharing.pay.entity.Refund;
import com.weesharing.pay.utils.FeeUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class HuiYuRefundData {

    @ApiModelProperty(value = "退款金额 单位元")
    private String refundAmount;

    @ApiModelProperty(value = "账户号")
    private String accountNo;

    @ApiModelProperty(value = "支付号")
    private String paymentNo;

    @ApiModelProperty(value = "工单号")
    private String workorderNo;

    public HuiYuRefundData(Refund refund){
        this.refundAmount = FeeUtils.Fen2Yuan(refund.getRefundFee());
        this.workorderNo = refund.getOutRefundNo();
        this.paymentNo = refund.getTradeNo();
        this.accountNo = refund.getCardNo();
    }

}
