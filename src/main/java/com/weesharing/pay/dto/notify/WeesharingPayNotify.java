package com.weesharing.pay.dto.notify;

import lombok.Data;

@Data
public class WeesharingPayNotify {
	
   private String requestNo;
   private String tradeType;
   private Double amount;
   private Double actualAmount;
   private Double payAmount;
   private String transactionTime;
   private String platformUserNo;
   private String platformMchNo;
   private String platformOrderNo;
   private String paymentMethod;
   private String payStatus;
   private String paymentNo;
   private String customDefine;

}
