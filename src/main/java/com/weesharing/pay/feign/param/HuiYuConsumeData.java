package com.weesharing.pay.feign.param;

import com.weesharing.pay.config.AggPayConfig;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.feign.BeanContext;
import com.weesharing.pay.utils.FeeUtils;
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
    private String receiveAccountNo;

    public HuiYuConsumeData(Consume consume) {

        this.orderNo = consume.getOrderNo();
        this.payAmount = FeeUtils.Fen2Yuan(consume.getActPayFee());
        this.accountNo = consume.getCardNo();
        this.password = consume.getCardPwd();
        this.receiveAccountNo = consume.getPayee();
    }

}
