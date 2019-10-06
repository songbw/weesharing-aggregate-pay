package com.weesharing.pay.feign.result;

import java.util.List;

import lombok.Data;

@Data
public class PaymentResult {

	private List<PayResult> cardPayResponseBeanList;

	public PaymentResult() {
	}
}
