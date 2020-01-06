package com.weesharing.pay.feign.result;

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

    private String createdAt;

    private String updatedAt;
    
    private String telephone ;
}