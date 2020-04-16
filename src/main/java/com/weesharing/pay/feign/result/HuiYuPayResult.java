package com.weesharing.pay.feign.result;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HuiYuPayResult {
    private String payAmount;
    private Integer accountType;
    private String accountTypeDesc;
    private String accountNo;
    private String serialNo;
}
