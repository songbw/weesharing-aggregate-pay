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
import com.weesharing.pay.service.IPreConsumeService;
import com.weesharing.pay.service.IPayService;

import cn.hutool.core.date.DateUtil;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ZhangPeng
 * @since 2019-09-18
 */
@Service
public class ConsumeServiceImpl extends ServiceImpl<ConsumeMapper, Consume> implements IConsumeService {

	@Autowired
	private IPreConsumeService preConsumeService;
	private IPayService wsPayService;
	
	/**
	 * 	1. 查询支付订单号
	 *  1. 比对实际支付价格
	 *  2. 更新支付订单的字段信息
	 *  3. 调用联机账户
	 * 
	 */
	@Override
	public void doPay(Consume consume) {
	
		Date now = new Date();
		QueryWrapper<PreConsume> consumeQuery = new QueryWrapper<PreConsume>();
		consumeQuery.eq("order_no", consume.getOrderNo());
		consumeQuery.between("create_date", DateUtil.offsetMinute(now, -30) , now);
		
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
		
		wsPayService.doPay(consume);
	}

}
