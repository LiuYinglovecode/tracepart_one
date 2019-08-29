package com.yunlu.controller;

import com.yunlu.core.config.ConfigClient;
import com.yunlu.dao.CoreDNSDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;
import com.yunlu.sercice.CoreDNSService;

import java.util.Map;

import static com.yunlu.utils.BuildResult.buildResult;


@RestController
@RequestMapping("/coredns")
@ComponentScan(basePackages = {"com.yunlu.sercice.impl"})
public class CoreDNSController {
    private static Logger LOGGER = LoggerFactory.getLogger(CoreDNSController.class);

    private final String filePath;
    private final String corednsBody;
    private final CoreDNSService coreDNSService;

    @Autowired
    public CoreDNSController(CoreDNSService coreDNSService, ConfigClient configClient) {
        this.coreDNSService = coreDNSService;
        filePath = configClient.get("app", "filepath");
        corednsBody = configClient.get("app", "corednsbody");
        CoreDNSDAO.dnsBody(corednsBody);
    }

    @RequestMapping(value = "/addcoredns", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> coreDNSTomysql(@RequestBody String coreDNS) {
        boolean isOK = false;
        int code = 0;
        try {
            isOK = false;
            code = 0;
            if (null != coreDNS) {
                isOK = coreDNSService.coreDNS(coreDNS, filePath, corednsBody);
                if (!isOK) {
                    code = 1;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return buildResult(code, isOK);
    }
}

