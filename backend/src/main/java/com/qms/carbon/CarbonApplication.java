package com.qms.carbon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // 开启定时任务支持
@SpringBootApplication
public class CarbonApplication {

    public static void main(String[] args) {

        SpringApplication.run(CarbonApplication.class, args
        );
    }

}
