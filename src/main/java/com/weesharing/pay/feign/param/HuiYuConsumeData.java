package com.weesharing.pay.feign.param;

import com.weesharing.pay.entity.Consume;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class HuiYuConsumeData {

    private String payAmount;
    private String accountNo;
    private String orderNo;
    private String password;

    public HuiYuConsumeData(Consume consume) {

        this.orderNo = consume.getOrderNo();
        this.payAmount = consume.getActPayFee();
        this.accountNo = consume.getCardNo();
        this.password = consume.getCardPwd();
    }

}
