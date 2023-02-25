package com.zhr.alibabanacosdiscovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AlibabaNacosDiscoveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlibabaNacosDiscoveryApplication.class, args);
    }

}
