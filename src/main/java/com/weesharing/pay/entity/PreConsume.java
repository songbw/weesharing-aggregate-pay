package com.weesharing.pay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author ZhangPeng
 * @since 2019-09-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wspay_pre_consume")
@ApiModel(value="PreConsume对象", description="")
public class PreConsume extends Model<PreConsume> {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "支付方式")
    private String payType;

    @ApiModelProperty(value = "付款方")
    private String payer;

    @ApiModelProperty(value = "收款方")
    private String payee;

    @ApiModelProperty(value = "支付订单号")
    private String orderNo;

    @ApiModelProperty(value = "订单号")
    private String outTradeNo;

    @ApiModelProperty(value = "联机账户订单号")
    private String tradeNo;

    @ApiModelProperty(value = "商品描述")
    private String body;

    @ApiModelProperty(value = "用户自定义")
    private String remark;

    @ApiModelProperty(value = "交易总金额")
    private String totalFee;

    @ApiModelProperty(value = "交易实际金额")
    private String actPayFee;

    @ApiModelProperty(value = "交易状态: 1: 成功, 2: 失败, 0: 新创建")
    private Integer status;

    @ApiModelProperty(value = "交易时间")
    private String tradeDate;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createDate;

    @ApiModelProperty(value = "支付限制")
    private String limitPay;

    @ApiModelProperty(value = "前端返回地址")
    private String returnUrl;

    @ApiModelProperty(value = "异步通知地址")
    private String notifyUrl;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
