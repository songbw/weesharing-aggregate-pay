package com.weesharing.pay.feign.param;

import java.util.Date;

import lombok.Data;

@Data
public class BalanceDetail {
	
    private Integer id;

    private Integer balanceId;

    private String openId;

    private String refundNo;

    private String orderNo;

    private Integer saleAmount;

    private Integer type;

    private Integer status;

    private Date createdAt;

    private Date updatedAt;
  
}