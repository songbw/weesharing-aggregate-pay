package com.weesharing.pay.service.impl;

import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weesharing.pay.dto.paytype.PayType;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.entity.PreConsume;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.mapper.ConsumeMapper;
import com.weesharing.pay.service.IConsumeService;
import com.weesharing.pay.service.IPayAsyncService;
import com.weesharing.pay.service.IPaySyncService;
import com.weesharing.pay.service.IPreConsumeService;
import com.weesharing.pay.service.impl.async.FcAliPayServiceImpl;
import com.weesharing.pay.service.impl.async.FcWxH5PayServiceImpl;
import com.weesharing.pay.service.impl.async.FcWxPayServiceImpl;
import com.weesharing.pay.service.impl.async.FcWxXcxPayServiceImpl;
import com.weesharing.pay.service.impl.async.PingAnPayServiceImpl;
import com.weesharing.pay.service.impl.sync.BalancePayServiceImpl;
import com.weesharing.pay.service.impl.sync.BankPayServiceImpl;
import com.weesharing.pay.service.impl.sync.WOAPayServiceImpl;
import com.weesharing.pay.service.impl.sync.WOCPayServiceImpl;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ZhangPeng
 * @since 2019-09-18
 */
@Slf4j
@Service
public class ConsumeServiceImpl extends ServiceImpl<ConsumeMapper, Consume> implements IConsumeService {

	@Autowired
	private IPreConsumeService preConsumeService;

	/**
	 * 	1. 查询支付订单号
	 *  1. 比对实际支付价格
	 *  2. 更新支付订单的字段信息
	 *  3. 调用联机账户
	 * 
	 */
	@Override
	public void doPay(Consume consume) {
		
		IPaySyncService wsPayService = null;
	
		if(consume.getPayType().equals(PayType.BALANCE.getName())){  
			wsPayService = new BalancePayServiceImpl();
		}
		if(consume.getPayType().equals(PayType.CARD.getName())){  
			wsPayService = new WOCPayServiceImpl();
		}
		if(consume.getPayType().equals(PayType.WOA.getName())){  
			wsPayService = new WOAPayServiceImpl();
		}
		if(consume.getPayType().equals(PayType.BANK.getName())){  
			wsPayService = new BankPayServiceImpl();
		}
		if(wsPayService != null) {
			wsPayService.doPay(consume);
		}else {
			log.info("[同步支付失败]  wsPayService is null");
			throw new ServiceException("不支持该支付方式");
		}
		
	}

	@Override
	public String doAsynPay(Consume consume) {
		
		IPayAsyncService wsPayAsyncService = null;
		
		if(consume.getPayType().equals(PayType.FCALIPAY.getName())){  
			wsPayAsyncService = new FcAliPayServiceImpl();
		}
		if(consume.getPayType().equals(PayType.FCWX.getName())){  
			wsPayAsyncService = new FcWxPayServiceImpl();
		}
		if(consume.getPayType().equals(PayType.FCWXH5.getName())){  
			wsPayAsyncService = new FcWxH5PayServiceImpl();
		}
		if(consume.getPayType().equals(PayType.FCWXXCX.getName())){  
			wsPayAsyncService = new FcWxXcxPayServiceImpl();
		}
		if(consume.getPayType().equals(PayType.PINGAN.getName())){  
			wsPayAsyncService = new PingAnPayServiceImpl();
		}
		if(wsPayAsyncService != null) {
			return wsPayAsyncService.doPay(consume);
		}else {
			log.info("[异步支付失败]  wsPayAsyncService is null");
			throw new ServiceException("不支持该支付方式");
		}
	}
	
	/**
	 * 	持久化消费记录
	 * @param consume
	 */
	@Override
	public void persistConsume(Consume consume) {
		Date now = new Date();
		QueryWrapper<PreConsume> consumeQuery = new QueryWrapper<PreConsume>();
		consumeQuery.eq("order_no", consume.getOrderNo());
		consumeQuery.between("create_date", formatDate(DateUtil.offsetMinute(now, -30)) , formatDate(DateUtil.offsetMinute(now, +1)));
		
		PreConsume one = preConsumeService.getOne(consumeQuery);
		if(one == null) {
			throw new ServiceException("请核实支付订单号和支付金额再支付或者重新获取支付订单号");
		}else if(one.getStatus() != 0){
			throw new ServiceException("该支付交易已处理过,请重新申请支付订单号");
		}
		consume.setOutTradeNo(one.getOutTradeNo());
		consume.setPayType(consume.getPayType());
		consume.setCreateDate(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
		consume.insertOrUpdate();
	}
	
	private String formatDate(Date date) {
		return DateUtil.format(date, "yyyy-MM-dd HH:mm:ss");
	}

}
