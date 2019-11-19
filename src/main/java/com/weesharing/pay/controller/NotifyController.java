package com.weesharing.pay.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weesharing.pay.dto.notify.PayNotify;
import com.weesharing.pay.dto.notify.WeesharingPayNotify;

import cn.hutool.json.JSONUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/wspay_notify")
public class NotifyController {
	
	@PostMapping("/weesharing_pay")
	@ApiOperation(value="凤巢支付回调接口")
	public String prePay(@RequestBody PayNotify<WeesharingPayNotify> notifyParam){
		log.info(JSONUtil.wrap(notifyParam, false).toString());
		return "SUCCESS";
	}

}
