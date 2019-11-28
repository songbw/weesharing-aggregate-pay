package com.weesharing.pay;

import com.weesharing.pay.dto.PrePay;
import com.weesharing.pay.dto.paytype.PayType;

import cn.hutool.json.JSONUtil;

public class JSONGen {
	
	public static void main(String[] args) {
		System.out.println(JSONUtil.wrap(PayType.values(), false));
	}

}
