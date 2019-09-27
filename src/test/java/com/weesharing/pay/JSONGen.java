package com.weesharing.pay;

import com.weesharing.pay.dto.PrePay;

import cn.hutool.json.JSONUtil;

public class JSONGen {
	
	public static void main(String[] args) {
		System.out.println(JSONUtil.wrap(new PrePay(), false));
	}

}
