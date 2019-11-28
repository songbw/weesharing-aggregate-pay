package com.weesharing.pay.dto.notify;

import lombok.Data;
import lombok.experimental.Accessors;
/**
 * 	凤巢支付回调信息
 *  @author zp
 *
 */
@Data
@Accessors(chain = true)
public class WeesharingPayNotify {

	private String serviceName;
	private String platformNo;
	private WeesharingRespData respData;
	private String sign;

}

@Data
@Accessors(chain = true)
class WeesharingRespData {

	private String requestNo;
	private String tradeType;
	private Double amount;
	private Double actualAmount;
	private Double payAmount;
	private String transactionTime;
	private String platformUserNo;
	private String platformMchNo;
	private String platformOrderNo;
	private String paymentMethod;
	private String payStatus;
	private String paymentNo;
	private String customDefine;
}
