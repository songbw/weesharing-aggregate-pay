package com.weesharing.pay.feign.config;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;

import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.weesharing.pay.common.CommonResult2;
import com.weesharing.pay.exception.ServiceException;

import cn.hutool.json.JSONUtil;
import feign.Response;
import feign.Util;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class BizExceptionFeignErrorDecoder implements feign.codec.ErrorDecoder{
	
    @Override
    public Exception decode(String methodKey, Response response) {
        if(response.status() != 200 ){
        	try {
				String resultStr = new String(Util.toByteArray(response.body().asInputStream()));
				log.info("[Feign Status Not 200]" + resultStr);
				CommonResult2<?> result = JSONUtil.toBean(resultStr, CommonResult2.class);
				return new HystrixBadRequestException(result.getMsg());
			} catch (IOException e) {
				throw new ServiceException("Feign Exception");
			}
        }
        return feign.FeignException.errorStatus(methodKey, response);
    }
}
