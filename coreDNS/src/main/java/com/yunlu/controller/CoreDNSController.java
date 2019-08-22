package com.yunlu.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;
import com.yunlu.sercice.CoreDNSService;

import java.util.Map;

import static com.yunlu.utils.BuildResult.buildResult;


@RestController
@RequestMapping("/core_dns")
@ComponentScan(basePackages = {"com.yunlu.sercice.impl"})
public class CoreDNSController {
    private static Logger LOGGER = LoggerFactory.getLogger(CoreDNSController.class);


    @Autowired
    CoreDNSService coreDNSService;

    @RequestMapping(value = "/tomysql", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> coreDNSTomysql(@RequestBody String coreDNS) {
        boolean isOK = false;
        int code = 0;
        if (null != coreDNS) {
            isOK = coreDNSService.coreDNS(coreDNS);
            if (!isOK) {
                code = 1;
            }
        }
        return buildResult(code, isOK);
    }
}

