package com.weesharing.pay.feign.param;

import java.math.BigDecimal;

import com.weesharing.pay.dto.pay.BankPay;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.feign.result.BankAuthResult;

import lombok.Data;

@Data
public class BankConsumeData {

	private String accountId;
	private String accountName;
	private String certNo;
	private String cvv2;
	private String expiredDate;
	private String mobileNo;
	private String accountType;
	private BigDecimal tranAmt;  //元
	private Integer authId;
	private String custId;
	private String merOrderId;
	private String phoneToken;
	private String verifyCode;
	private String orderId;
	
	public BankConsumeData(BankPay pay, BankAuthResult auth, Consume consume) {
		
		this.accountId = pay.getAccountId();
		this.accountName = pay.getAccountName();
		this.certNo = pay.getCertNo();
		this.cvv2 = pay.getCvv2();
		this.expiredDate = pay.getExpiredDate();
		this.mobileNo = pay.getMobileNo();
		this.tranAmt = new BigDecimal(pay.getActPayFee()).divide(BigDecimal.valueOf(100)).setScale(2,BigDecimal.ROUND_HALF_EVEN);
		this.accountType = pay.getAccountType();
		this.verifyCode = pay.getVerifyCode();
//		this.orderId = consume.getOutTradeNo();
		this.orderId = consume.getOrderNo();
		
		this.authId = auth.getId();
		this.custId = auth.getCustId();
		this.merOrderId = auth.getMerOrderId();
		this.phoneToken = auth.getPhoneToken();
	}
}
