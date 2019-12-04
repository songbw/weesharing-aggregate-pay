package com.weesharing.pay;

import com.weesharing.pay.dto.paytype.PayType;

public class JSONGen {
	
	public static void main(String[] args) {
		
		
		System.out.println(PayType.valueOf("card".toUpperCase()).getPay().equals("async"));
//		System.out.println(JSONUtil.wrap(PayType.values(), false));
	}

}
