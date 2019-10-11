package com.weesharing.pay.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weesharing.pay.dto.pay.PayType;
import com.weesharing.pay.entity.Refund;
import com.weesharing.pay.mapper.RefundMapper;
import com.weesharing.pay.service.IRefundService;
import com.weesharing.pay.service.IPayService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ZhangPeng
 * @since 2019-09-18
 */
@Service
public class RefundServiceImpl extends ServiceImpl<RefundMapper, Refund> implements IRefundService {

	private IPayService wsPayService;
	
	@Override
	public void doRefund(Refund refund) {
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
		wsPayService.doRefund(refund);
		
	}

}
