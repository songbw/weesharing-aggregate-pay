package com.weesharing.pay.feign.param;

import java.util.Date;

import lombok.Data;

@Data
public class QueryBalanceData {
	
    private Integer id;

    private Integer userId;

    private String telephone;

    private Integer amount;

    private String openId;

    private Integer status;

    private Date createdAt;

    private Date updatedAt;
  
}