package com.weesharing.pay;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@MapperScan("com.weesharing.pay.mapper")
public class WSPAYApplication {
	public static void main(String[] args) {
		SpringApplication.run(WSPAYApplication.class, args);
	}
}