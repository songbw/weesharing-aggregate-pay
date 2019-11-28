package com.weesharing.pay.service;

import java.util.List;

import com.weesharing.pay.dto.paytype.MutilPayType;

public interface IPayTypeService {
	
	public void addPayTypeToAppId(List<MutilPayType> payTypes);
	
	public void delPayTypeByAppId(List<MutilPayType> payTypes);
	
	public List<MutilPayType> getPayTypeByAppID(String appid);

}
