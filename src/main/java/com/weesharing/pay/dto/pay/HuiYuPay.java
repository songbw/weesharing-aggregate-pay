package com.weesharing.pay.dto.pay;

import com.weesharing.pay.entity.Consume;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class HuiYuPay extends BasePayBean{

    @ApiModelProperty(value = "账户")
    @NotBlank(message = "账户")
    private String cardNo;

    @ApiModelProperty(value = "密码")
    @NotBlank(message = "密码")
    private String cardPwd;


    public Consume convert() {
        Consume consume  = new Consume();
        consume.setPayType(this.getPayType());
        consume.setOrderNo(this.getOrderNo());
        consume.setActPayFee(this.getActPayFee());
        consume.setCardNo(getCardNo());
        consume.setCardPwd(getCardPwd());
        return consume;
    }

}
