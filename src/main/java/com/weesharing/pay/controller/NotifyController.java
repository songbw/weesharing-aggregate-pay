package com.weesharing.pay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weesharing.pay.common.CommonResult;
import com.weesharing.pay.dto.notify.CommonPayNotify;
import com.weesharing.pay.dto.notify.CommonRefundNotify;
import com.weesharing.pay.dto.notify.WeesharingPayNotify;
import com.weesharing.pay.service.handler.NotifyPayHandler;
import com.weesharing.pay.service.handler.NotifyRefundHandler;

import cn.hutool.json.JSONUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/wspay_notify")
public class NotifyController {
	
	@Autowired
	private NotifyPayHandler payHandler;
	
	@Autowired
	private NotifyRefundHandler refundHandler;
	
	@PostMapping("/weesharing_pay")
	@ApiOperation(value="凤巢支付回调接口", notes="用于深圳唐荣团队支付系统")
	public String FcSzPay(@RequestBody WeesharingPayNotify notifyParam){
		log.info(JSONUtil.wrap(notifyParam, false).toString());
		return "SUCCESS";
	}
	
	@PostMapping("/bj/weesharing_pay")
	@ApiOperation(value="凤巢北京支付回调接口", notes="用于凤巢北京团队支付系统")
	public CommonResult<String> FcBjPay(@RequestBody CommonPayNotify notifyParam){
		log.info("[北京支付回调]:{}", JSONUtil.wrap(notifyParam, false).toString());
		payHandler.PayNotifyService(notifyParam);
		return CommonResult.success();
	}
	
	@PostMapping("/bj/weesharing_refund")
	@ApiOperation(value="凤巢北京退款回调接口", notes="用于凤巢北京团队支付系统")
	public CommonResult<String> FcBjPay(@RequestBody CommonRefundNotify notifyParam){
		log.info("[北京退款回调]:{}", JSONUtil.wrap(notifyParam, false).toString());
		refundHandler.RefundNotifyService(notifyParam);
		return CommonResult.success();
	}

}
