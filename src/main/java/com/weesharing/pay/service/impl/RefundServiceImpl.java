package com.weesharing.pay.service.impl;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weesharing.pay.dto.paytype.PayType;
import com.weesharing.pay.entity.Refund;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.mapper.RefundMapper;
import com.weesharing.pay.service.IPayAsyncService;
import com.weesharing.pay.service.IPaySyncService;
import com.weesharing.pay.service.IRefundService;
import com.weesharing.pay.service.impl.async.BankPayAsyncServiceImpl;
import com.weesharing.pay.service.impl.async.FcWxH5PayServiceImpl;
import com.weesharing.pay.service.impl.async.FcWxPayServiceImpl;
import com.weesharing.pay.service.impl.async.FcWxXcxPayServiceImpl;
import com.weesharing.pay.service.impl.async.YunChengPayServiceImpl;
import com.weesharing.pay.service.impl.sync.BalancePayServiceImpl;
import com.weesharing.pay.service.impl.sync.FcAliPaySyncServiceImpl;
import com.weesharing.pay.service.impl.sync.PingAnPaySyncServiceImpl;
import com.weesharing.pay.service.impl.sync.WOAPayServiceImpl;
import com.weesharing.pay.service.impl.sync.WOCPayServiceImpl;
import com.weesharing.pay.service.impl.sync.HuiYuPayServiceImpl;

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
public class RefundServiceImpl extends ServiceImpl<RefundMapper, Refund> implements IRefundService {

	/**
	 * 同步退款
	 */
	@Override
	public void doRefund(Refund refund) {
		log.info("同步退款处理 参数： {}", JSONUtil.toJsonStr(refund));
		IPaySyncService wsPayService = null;
		//记录退款记录
		refund.insert();

		if(refund.getPayType().equals(PayType.BALANCE.getName())){
			wsPayService = new BalancePayServiceImpl();
		}
		if(refund.getPayType().equals(PayType.CARD.getName())){
			wsPayService = new WOCPayServiceImpl();
		}
		if(refund.getPayType().equals(PayType.WOA.getName())){
			wsPayService = new WOAPayServiceImpl();
		}
		if(refund.getPayType().equals(PayType.PINGAN.getName())){
			wsPayService = new PingAnPaySyncServiceImpl();
		}
		if(refund.getPayType().equals(PayType.FCALIPAY.getName()) ||
				refund.getPayType().equals(PayType.FCALIJSSDK.getName())){
			wsPayService = new FcAliPaySyncServiceImpl();
		}
		if(refund.getPayType().equals(PayType.HUIYU.getName())){
			wsPayService = new HuiYuPayServiceImpl();
		}
		if(wsPayService != null) {
			wsPayService.doRefund(refund);
		}else {
			throw new ServiceException("[同步退款]: 不支持此退款方式");
		}

	}

	/**
	 * 异步退款
	 */
	@Override
	public String doAsyncRefund(Refund refund) {

		IPayAsyncService wsPayAsynService = null;
		//记录退款记录
		refund.insert();
		log.info("异步退款处理 新建refund： {}",JSONUtil.toJsonStr(refund));
		if(refund.getPayType().equals(PayType.FCWX.getName())){
			wsPayAsynService = new FcWxPayServiceImpl();
		}
		if(refund.getPayType().equals(PayType.FCWXH5.getName())){
			wsPayAsynService = new FcWxH5PayServiceImpl();
		}
		if(refund.getPayType().equals(PayType.FCWXXCX.getName())){
			wsPayAsynService = new FcWxXcxPayServiceImpl();
		}
		if(refund.getPayType().equals(PayType.BANK.getName())){
			wsPayAsynService = new BankPayAsyncServiceImpl();
		}
		if(refund.getPayType().equals(PayType.YUNCHENG.getName())){
			wsPayAsynService = new YunChengPayServiceImpl();
		}

		if(wsPayAsynService != null) {
			return wsPayAsynService.doRefund(refund);
		}else {
			throw new ServiceException("[异步退款]: 不支持此退款方式");
		}
	}

}
