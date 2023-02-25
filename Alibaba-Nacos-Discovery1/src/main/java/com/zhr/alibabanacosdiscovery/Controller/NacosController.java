package com.zhr.alibabanacosdiscovery.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 20179
 */
@RestController
public class NacosController {

    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/nacos/discovery")
    public String echo() {
        return serverPort;
    }
}
