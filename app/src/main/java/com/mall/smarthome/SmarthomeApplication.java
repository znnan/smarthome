package com.mall.smarthome;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
@MapperScan(value = "com.mall.smarthome.dao")
public class SmarthomeApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmarthomeApplication.class, args);
    }

}
