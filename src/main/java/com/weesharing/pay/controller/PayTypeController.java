package com.weesharing.pay.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.weesharing.pay.common.CommonResult;
import com.weesharing.pay.dto.paytype.MutilPayType;
import com.weesharing.pay.dto.paytype.PayType;
import com.weesharing.pay.dto.paytype.PayTypeParam;
import com.weesharing.pay.service.IPayTypeService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/paytype")
public class PayTypeController {
	
	@Autowired
	private IPayTypeService payTypeService;

	@GetMapping("/all")
	@ApiOperation(value = "所有支持的支付方式")
	public CommonResult<List<Map<String, String>>> allPayType() {
		
		List<Map<String, String>> payTypes = Lists.newArrayList();
		for(PayType payType : PayType.values()) {
			Map<String, String> type = Maps.newHashMap();
			type.put("name", payType.getName());
			type.put("desc", payType.getDesc());
			payTypes.add(type);
		}
		return CommonResult.success(payTypes);
	}
	
	@PostMapping("/add")
	@ApiOperation(value = "添加支付方式到AppID")
	public CommonResult<String> addPayTypeToAppID(@RequestBody @Valid PayTypeParam payTypes) {
		payTypeService.addPayTypeToAppId(payTypes.getMutilPayTypes());
		return CommonResult.success();
	}
	
	@DeleteMapping("/del")
	@ApiOperation(value = "删除AppID的支付方式")
	public CommonResult<String> delPayTypeToAppID(@RequestBody @Valid PayTypeParam payTypes) {
		payTypeService.delPayTypeByAppId(payTypes.getMutilPayTypes());
		return CommonResult.success();
	}
	
	@GetMapping("/query")
	@ApiOperation(value = "查询AppID支持的支付方式")
	public CommonResult<List<MutilPayType>> getPayTypeByAppID(String appid) {
		List<MutilPayType> payTypes = payTypeService.getPayTypeByAppID(appid);
		return CommonResult.success(payTypes);
	}
}
