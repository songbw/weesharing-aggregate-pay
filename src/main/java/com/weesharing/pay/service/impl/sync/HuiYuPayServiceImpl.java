package com.weesharing.pay.service.impl.sync;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.weesharing.pay.common.CommonResult2;
import com.weesharing.pay.entity.Consume;
import com.weesharing.pay.entity.Refund;
import com.weesharing.pay.exception.ServiceException;
import com.weesharing.pay.feign.BeanContext;
import com.weesharing.pay.feign.HuiYuPayService;
import com.weesharing.pay.feign.param.HuiYuConsumeData;
import com.weesharing.pay.feign.param.HuiYuRefundData;
import com.weesharing.pay.feign.result.HuiYuPayResult;
import com.weesharing.pay.feign.result.HuiYuRefundResult;
import com.weesharing.pay.service.IPaySyncService;
import com.weesharing.pay.utils.AggPayTradeDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service(value="huiYuPayService")
public class HuiYuPayServiceImpl implements IPaySyncService {

    @Override
    public void doPay(Consume consume) {
        HuiYuConsumeData consumeData = new HuiYuConsumeData(consume);
        CommonResult2<HuiYuPayResult> commonResult = null;
        try {
            commonResult = BeanContext.getBean(HuiYuPayService.class).payment(consumeData);
        }catch(Exception e) {
            e.printStackTrace();
        }
        log.info("请求惠余支付参数:{}, 结果: {}", JSONUtil.wrap(consumeData, false), JSONUtil.wrap(commonResult, false));
        ///不记录用户支付密码
        consume.setCardPwd("");

        if (200 == commonResult.getCode() && null != commonResult.getData()) {
            consume.setTradeDate(AggPayTradeDate.buildTradeDate());
            consume.setStatus(1);
            consume.setTradeNo(commonResult.getData().getSerialNo());
            consume.insertOrUpdate();
        } else {
            consume.setStatus(2);
            consume.insertOrUpdate();
            throw new ServiceException(commonResult.getMsg());
        }

    }

    @Override
    public void doRefund(Refund refund) {
        log.info("惠余退款 入参： {}", JSONUtil.toJsonStr(refund));
        HuiYuRefundData refundData = new HuiYuRefundData(refund);
        CommonResult2<HuiYuRefundResult> commonResult = BeanContext.getBean(HuiYuPayService.class).refund(refundData);
        log.info("请求惠余退款服务调用 参数: {}, 结果: {}", JSONUtil.wrap(refundData, false), JSONUtil.wrap(commonResult, false));
        if (200 == commonResult.getCode()) {
            refund.setTradeDate(AggPayTradeDate.buildTradeDate());
            refund.setStatus(1);
            refund.insertOrUpdate();
        } else {
            refund.setStatus(2);
            refund.insertOrUpdate();
            throw new ServiceException(commonResult.getMsg());
        }
    }
}
