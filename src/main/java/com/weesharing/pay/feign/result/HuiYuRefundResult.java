package com.weesharing.pay.feign.result;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HuiYuRefundResult {

    private String refundAmount;
    private Integer accountType;

}
