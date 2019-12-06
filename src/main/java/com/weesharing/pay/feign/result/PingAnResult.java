package com.weesharing.pay.feign.result;

import lombok.Data;

@Data
public class PingAnResult {

    private String orderNo;
    
    private String merchantNo;
    
    public PingAnPrePay convert(String mchOrderNo, String memberNo) {
    	PingAnPrePay pay = new PingAnPrePay();
    	pay.setMchOrderNo(mchOrderNo);
    	pay.setPayId(memberNo);
    	pay.setMerchantNo(this.getMerchantNo());
    	return pay;
    }
 
}

@Data
class PingAnPrePay{
	
	private String mchOrderNo;

    private String payId;
    
    private String merchantNo;
    
}