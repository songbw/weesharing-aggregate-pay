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
@TableName("wspay_pre_refund")
@ApiModel(value="PreRefund对象", description="")
public class PreRefund extends Model<PreRefund> {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "退款号")
    private String outRefundNo;

    @ApiModelProperty(value = "原订单号")
    private String sourceOutTradeNo;

    @ApiModelProperty(value = "支付订单号")
    private String orderNo;

    @ApiModelProperty(value = "商户编号")
    private String merchantCode;

    @ApiModelProperty(value = "交易总金额")
    private String totalFee;

    @ApiModelProperty(value = "交易实际金额")
    private String refundFee;

    @ApiModelProperty(value = "退款状态: 1: 成功, 2: 失败, 0: 新创建")
    private Integer status;

    @ApiModelProperty(value = "退款时间")
    private String tradeDate;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createDate;

    @ApiModelProperty(value = "前端返回地址")
    private String returnUrl;

    @ApiModelProperty(value = "异步通知地址")
    private String notifyUrl;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
