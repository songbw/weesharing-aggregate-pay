package com.weesharing.pay.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * 读取项目相关配置
 * 
 * @author zp
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "aggpay")
public class AggPayConfig
{
    /** 云城APP MerchantNo */
    private String icloudcity;
    
    /** 云城APP NotifyUrl */
    private String icloudcityNotifyUrl;

}