package com.weesharing.pay.feign.param;

import com.weesharing.pay.entity.Refund;

import lombok.Data;

@Data
public class PingAnRefundData {

    private String refundMchOrderNo ;
    private String oriOrderNo ;
    private Integer refundAmt ;
    
    public PingAnRefundData() {
	}
    
	public PingAnRefundData(Refund refund) {
		this.oriOrderNo = refund.getOrderNo();
		this.refundMchOrderNo = refund.getOutRefundNo();
		this.refundAmt = Integer.parseInt(refund.getRefundFee());
	}

}