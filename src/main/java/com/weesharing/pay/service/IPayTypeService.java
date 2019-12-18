package com.weesharing.pay.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.weesharing.pay.dto.paytype.MutilPayType;
import com.weesharing.pay.entity.PayTypeEntity;

public interface IPayTypeService extends IService<PayTypeEntity>{
	
	public void addPayTypeToAppId(List<MutilPayType> payTypes);
	
	public void delPayTypeByAppId(String ids);
	
	public List<MutilPayType> getPayTypeByAppID(String appid);

}
