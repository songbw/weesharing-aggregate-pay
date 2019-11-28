package com.weesharing.pay.feign.param;

import java.math.BigDecimal;

import com.weesharing.pay.dto.pay.BankAuthBean;

import lombok.Data;

@Data
public class BankAuthBeanData {
	
	private String accountId;
	private String accountName;
	private String certNo;
	private String cvv2;
	private String doSaveIt;
	private String expiredDate;
	private String mobileNo;
	private String openId;
	private BigDecimal tranAmt;  //元
	
	public BankAuthBeanData(BankAuthBean auth) {
		
		this.accountId = auth.getAccountId();
		this.accountName = auth.getAccountName();
		this.certNo = auth.getCertNo();
		this.cvv2 = auth.getCvv2();
		this.doSaveIt = auth.getDoSaveIt();
		this.expiredDate = auth.getExpiredDate();
		this.mobileNo = auth.getMobileNo();
		this.openId = auth.getOpenId();
		this.tranAmt = new BigDecimal(auth.getTranAmt()).divide(BigDecimal.valueOf(100)).setScale(2,BigDecimal.ROUND_HALF_EVEN);
	}
	
	

}
