package com.yunlu.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunlu.core.api.ApiResult;
import com.yunlu.core.config.ConfigClient;
import com.yunlu.dao.CoreDNSDAO;
import com.yunlu.utils.WriteUtil;
import com.yunlu.utils.mysql.MySqlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;
import com.yunlu.sercice.CoreDNSService;

import java.io.Serializable;


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

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult addcoreDNS(@RequestBody String coreDNS) {
        try {
            if (null != coreDNS) {
                if (coreDNSService.addcoreDNS(coreDNS, filePath, corednsBody)) {
                    return new ApiResult<>(0, "add success", coreDNS);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return new ApiResult<>(0, "add fail", coreDNS);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult delcoreDNS(@RequestBody String coreDNS) {
        try {
            if (null != coreDNS) {
                if (coreDNSService.deletecoreDNS(coreDNS, filePath, corednsBody)) {
                    return new ApiResult<>(0, "del success", coreDNS);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return new ApiResult<>(0, "del fail", coreDNS);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult updatecoreDNS(@RequestBody String coreDNS) {
        try {
            if (null != coreDNS) {
                if (coreDNSService.updatecoreDNS(coreDNS, filePath, corednsBody)) {
                    return new ApiResult<>(0, "update success", coreDNS);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return new ApiResult<>(0, "update fail", coreDNS);
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public String getcoreDNS(@RequestParam(name = "domainname") String address) {
        try {
            if (null != address) {
                JSONObject object = coreDNSService.getcoreDNS(address);
                return String.valueOf(object.get("address")) + "\t"
                        + String.valueOf(object.get("dnsin") + "\t")
                        + String.valueOf(object.get("dnstype")) + "\t"
                        + String.valueOf(object.get("ip"));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    @RequestMapping(value = "/getlist", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public String getcoreDNS() {
        try {
            String str = "";
            JSONArray array = coreDNSService.getListcoreDNS();
            for (Object l : array) {
                JSONObject object = JSONObject.parseObject(String.valueOf(l));
                if (!corednsBody.equals(object.get("address"))) {
                    str = str + String.valueOf(object.get("address")) + "\t"
                            + String.valueOf(object.get("dnsin") + "\t")
                            + String.valueOf(object.get("dnstype")) + "\t"
                            + String.valueOf(object.get("ip")) + "\n";
                }
            }
            return str;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }
}

