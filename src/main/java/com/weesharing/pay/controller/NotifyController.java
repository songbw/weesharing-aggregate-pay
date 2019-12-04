package com.weesharing.pay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weesharing.pay.dto.notify.CommonPayNotify;
import com.weesharing.pay.dto.notify.WeesharingPayNotify;
import com.weesharing.pay.service.handler.NotifyPayHandler;

import cn.hutool.json.JSONUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/wspay_notify")
public class NotifyController {
	
	@Autowired
	private NotifyPayHandler payHandler;
	
	@PostMapping("/weesharing_pay")
	@ApiOperation(value="凤巢支付回调接口")
	public String FcSzPay(@RequestBody WeesharingPayNotify notifyParam){
		log.info(JSONUtil.wrap(notifyParam, false).toString());
		return "SUCCESS";
	}
	
	@PostMapping("/bj/weesharing_pay")
	@ApiOperation(value="凤巢BJ支付回调接口")
	public String FcBjPay(@RequestBody CommonPayNotify notifyParam){
		log.info(JSONUtil.wrap(notifyParam, false).toString());
		payHandler.PayNotifyService(notifyParam);
		return "SUCCESS";
	}

}
