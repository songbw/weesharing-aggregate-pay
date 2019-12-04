package com.weesharing.pay.dto.paytype;

/**
 * 	支持的支付方式
 * @author zp
 *
 */
public enum PayType {
	
	BALANCE ("balance",  "惠民商城余额",         "sync"),
	CARD    ("card",     "惠民优选卡",           "sync"), 
	WOA     ("woa",      "惠民商城联机账户",     "sync"), 
	BANK    ("bank",     "中投快捷支付",         "sync"),           
	PINGAN  ("pingan",   "惠民商城平安统一支付", "async"),       
	FCWXH5  ("fcwxh5",   "凤巢微信H5支付",       "async"),      
	FCALIPAY("fcalipay", "凤巢支付宝H5支付",     "async"),
	FCWX    ("fcwx",     "凤巢微信公众号支付",   "async"),
	FCWXXCX ("fcwxxcx",  "凤巢微信小程序支付",   "async");
	
	private String name;
	private String desc;
	private String way;
	
	private PayType(String name, String desc, String way) {
		this.name = name;
		this.desc = desc;
		this.way = way;
	}

	public String getName() {
		return name;
	}
	
	public String getWay() {
		return way;
	}

	public String getDesc() {
		return desc;
	}
}
