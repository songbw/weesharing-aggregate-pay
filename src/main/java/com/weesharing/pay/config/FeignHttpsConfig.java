package com.weesharing.pay.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Client;
import feign.Feign;
import feign.Logger;

@Configuration
public class FeignHttpsConfig {

    @Bean
    public Feign.Builder feignBuilder() {
        final Client trustSSLSockets = client();
        return Feign.builder().client(trustSSLSockets);
    }

    @Bean
    public Client client(){
        return new Client.Default(
                TrustingSSLSocketFactory.get(), new NoopHostnameVerifier());
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

}
