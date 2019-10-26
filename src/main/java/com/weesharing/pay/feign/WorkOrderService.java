package com.weesharing.pay.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.weesharing.pay.dto.RefundResult;
import com.weesharing.pay.feign.hystric.WorkOrderServiceH;

@FeignClient(value = "workorders", fallback = WorkOrderServiceH.class)
public interface WorkOrderService {

    @RequestMapping(value = "/aggpays/notify", method = RequestMethod.POST)
    void refundNotify(@RequestBody RefundResult data);
}
