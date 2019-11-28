package com.weesharing.pay.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.weesharing.pay.dto.paytype.MutilPayType;
import com.weesharing.pay.service.IPayTypeService;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PayTypeServiceImpl implements IPayTypeService {

	@Override
	public void addPayTypeToAppId(List<MutilPayType> payTypes) {
		log.info(JSONUtil.wrap(payTypes, false).toString());
	}

	@Override
	public void delPayTypeByAppId(List<MutilPayType> payTypes) {
		log.info(JSONUtil.wrap(payTypes, false).toString());

	}

	@Override
	public List<MutilPayType> getPayTypeByAppID(String appid) {
		log.info("appid:{}", appid);
		return null;
	}

}
