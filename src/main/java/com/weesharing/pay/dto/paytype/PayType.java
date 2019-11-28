package com.weesharing.pay.dto.paytype;

/**
 * 	支持的支付方式
 * @author zp
 *
 */
public enum PayType {
	
	BALANCE ("balance",  "惠民商城余额"),
	CARD    ("card",     "惠民优选卡"), 
	WOA     ("woa",      "惠民商城联机账户"), 
	BANK    ("bank",     "中投快捷支付"),           
	PINGAN  ("pingan",   "惠民商城平安统一支付"),       
	FCWXH5  ("fcwxh5",   "凤巢微信H5支付"),      
	FCALIPAY("fcalipay", "凤巢支付宝H5支付"),
	FCWX    ("fcwx",     "凤巢微信公众号支付");
	
	private String name;
	private String desc;
	
	private PayType(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}

	public String getName() {
		return name;
	}
	
	public String getDesc() {
		return desc;
	}

	public void setName(String name) {
		this.name = name;
	}

}
