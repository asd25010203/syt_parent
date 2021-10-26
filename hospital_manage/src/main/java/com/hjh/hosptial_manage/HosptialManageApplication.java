package com.hjh.hosptial_manage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "com.hjh")
@EnableDiscoveryClient
public class HosptialManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(HosptialManageApplication.class, args);
    }

}
