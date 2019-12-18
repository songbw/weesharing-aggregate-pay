package com.weesharing.pay.service.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.weesharing.pay.dto.paytype.MutilPayType;
import com.weesharing.pay.entity.PayTypeEntity;
import com.weesharing.pay.mapper.PayTypeMapper;
import com.weesharing.pay.service.IPayTypeService;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PayTypeServiceImpl extends ServiceImpl<PayTypeMapper, PayTypeEntity> implements IPayTypeService {

	@Override
	public void addPayTypeToAppId(List<MutilPayType> payTypes) {
		List<PayTypeEntity> entityList = Lists.newArrayList();
		payTypes.forEach(pay -> {
			entityList.add(pay.convert());
		});
		log.info(JSONUtil.wrap(entityList, false).toString());
		saveBatch(entityList);
	}

	@Override
	public void delPayTypeByAppId(String ids) {
		removeByIds(Arrays.asList(ids.split(",")));
	}

	@Override
	public List<MutilPayType> getPayTypeByAppID(String appid) {
		QueryWrapper<PayTypeEntity> query = new QueryWrapper<PayTypeEntity>();
		query.eq("appid", appid);
		List<MutilPayType> entityList = Lists.newArrayList();
		list(query).forEach(pay -> {
			entityList.add(pay.convert());
		});
		return entityList;
	}
}
