package com.weesharing.pay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.weesharing.pay.entity.Consume;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ZhangPeng
 * @since 2019-09-18
 */
public interface IConsumeService extends IService<Consume> {
	
	public void doPay(Consume consume);
	
}
