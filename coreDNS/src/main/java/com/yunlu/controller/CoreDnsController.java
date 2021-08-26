package com.yunlu.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunlu.core.api.ApiResult;
import com.yunlu.core.config.ConfigClient;
import com.yunlu.dao.CoreDnsDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;
import com.yunlu.sercice.CoreDnsService;

import java.util.Map;


/**
 * @author liyujie
 */
@RestController
@RequestMapping("/coreDns")
@ComponentScan(basePackages = {"com.yunlu.sercice.impl"})
public class CoreDnsController {
    private static Logger LOGGER = LoggerFactory.getLogger(CoreDnsController.class);

    private final String filePath1;
    private final String filePath2;
    private final String baseDns1;
    private final String baseDns2;
    private final String tableName1;
    private final String tableName2;
    private final CoreDnsService coreDnsService;

    @Autowired
    public CoreDnsController(CoreDnsService coreDnsService, ConfigClient configClient) {
        this.coreDnsService = coreDnsService;
        filePath1 = configClient.get("app", "filePath1");
        filePath2 = configClient.get("app", "filePath2");
        baseDns1 = configClient.get("app", "baseDns1");
        baseDns2 = configClient.get("app", "baseDns2");
        tableName1 = configClient.get("app", "tableName1");
        tableName2 = configClient.get("app", "tableName2");
        CoreDnsDAO.initDns(baseDns1, tableName1);
        CoreDnsDAO.initDns(baseDns2, tableName2);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult addCoreDns(@RequestBody Map<String, String> coreDns) {
        try {
            if (null != coreDns) {
                String baseDns = coreDns.get("domainName");
                String dns = coreDns.get("dns");
                if (baseDns1.equals(baseDns)) {
                    if (coreDnsService.addCoreDns(baseDns, dns, filePath1, tableName1)) {
                        return new ApiResult<>(0, "add success", dns);
                    }
                } else if (baseDns2.equals(baseDns)) {
                    if (coreDnsService.addCoreDns(baseDns, dns, filePath2, tableName2)) {
                        return new ApiResult<>(0, "add success", dns);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return new ApiResult<>(0, "add fail", coreDns);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult delCoreDns(@RequestBody Map<String, String> coreDns) {
        try {
            if (null != coreDns) {
                String baseDns = coreDns.get("domainName");
                String dns = coreDns.get("dns");
                if (baseDns1.equals(baseDns)) {
                    if (coreDnsService.deleteCoreDns(baseDns, dns, filePath1, tableName1)) {
                        return new ApiResult<>(0, "delete success", dns);
                    }
                } else if (baseDns2.equals(baseDns)) {
                    if (coreDnsService.deleteCoreDns(baseDns, dns, filePath2, tableName2)) {
                        return new ApiResult<>(0, "delete success", dns);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return new ApiResult<>(0, "del fail", coreDns);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult updateCoreDns(@RequestBody Map<String, String> coreDns) {
        try {
            if (null != coreDns) {
                String baseDns = coreDns.get("domainName");
                String dns = coreDns.get("dns");
                if (baseDns1.equals(baseDns)) {
                    if (coreDnsService.updateCoreDns(baseDns, dns, filePath1, tableName1)) {
                        return new ApiResult<>(0, "update success", dns);
                    }
                } else if (baseDns2.equals(baseDns)) {
                    if (coreDnsService.updateCoreDns(baseDns, dns, filePath2, tableName2)) {
                        return new ApiResult<>(0, "update success", dns);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return new ApiResult<>(0, "update fail", coreDns);
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public String getCoreDns(@RequestParam(name = "domainName") String domainName, @RequestParam(name = "address") String address) {
        try {
            if (null != domainName && null != address) {
                if (baseDns1.equals(domainName)) {
                    JSONObject object = coreDnsService.getCoreDns(address, tableName1);
                    return String.valueOf(object.get("address")) + "\t"
                            + String.valueOf(object.get("dnsin")) + "\t"
                            + String.valueOf(object.get("dnstype")) + "\t"
                            + String.valueOf(object.get("ip"));
                } else if (baseDns2.equals(domainName)) {
                    JSONObject object = coreDnsService.getCoreDns(address, tableName2);
                    return String.valueOf(object.get("address")) + "\t"
                            + String.valueOf(object.get("dnsin")) + "\t"
                            + String.valueOf(object.get("dnstype")) + "\t"
                            + String.valueOf(object.get("ip"));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return "getCoreDns fail";
    }

    @RequestMapping(value = "/getList", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public String getCoreDnsList(@RequestParam(name = "domainName") String domainName) {
        try {
            if (null != domainName) {
                if (baseDns1.equals(domainName)) {
                    String str = "";
                    JSONArray array = coreDnsService.getCoreDnsList(tableName1);
                    for (Object l : array) {
                        JSONObject object = JSONObject.parseObject(String.valueOf(l));
                        if (!baseDns1.equals(object.get("address"))) {
                            str = str + String.valueOf(object.get("address")) + "\t"
                                    + String.valueOf(object.get("dnsin")) + "\t"
                                    + String.valueOf(object.get("dnstype")) + "\t"
                                    + String.valueOf(object.get("ip")) + "\n";
                        }
                    }
                    return str;
                } else if (baseDns2.equals(domainName)) {
                    String str = "";
                    JSONArray array = coreDnsService.getCoreDnsList(tableName2);
                    for (Object l : array) {
                        JSONObject object = JSONObject.parseObject(String.valueOf(l));
                        if (!baseDns1.equals(object.get("address"))) {
                            str = str + String.valueOf(object.get("address")) + "\t"
                                    + String.valueOf(object.get("dnsin")) + "\t"
                                    + String.valueOf(object.get("dnstype")) + "\t"
                                    + String.valueOf(object.get("ip")) + "\n";
                        }
                    }
                    return str;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return "getCoreDnsList fail";
    }

    @RequestMapping(value = "/getBaseDns", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public String getBaseDnsList() {
        try {
            return baseDns1 + "\t"
                    + baseDns2;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return "getCoreDns fail";
    }
}

