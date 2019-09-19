package com.weesharing.pay;

import com.weesharing.pay.dto.PayDTO;
import com.weesharing.pay.dto.PrePayDTO;
import com.weesharing.pay.dto.RefundDTO;

import cn.hutool.json.JSONUtil;

public class JSONGen {
	
	public static void main(String[] args) {
		System.out.println(JSONUtil.wrap(new PrePayDTO(), false));
		System.out.println(JSONUtil.wrap(new PayDTO(), false));
		System.out.println(JSONUtil.wrap(new RefundDTO(), false));
	}

}
