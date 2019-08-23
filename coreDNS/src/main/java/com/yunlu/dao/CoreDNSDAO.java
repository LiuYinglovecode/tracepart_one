package com.yunlu.dao;

import com.alibaba.fastjson.JSONObject;
import com.yunlu.utils.mysql.ToMySQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CoreDNSDAO {
    private static Logger LOGGER = LoggerFactory.getLogger(CoreDNSDAO.class);
    private static java.util.Map<String, String> Map = null;

    public static boolean toMysql(JSONObject coreDNS) {
        try {
            Map = (java.util.Map) coreDNS;
            return ToMySQL.coreDNSToMysql(Map);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }

    public static void dnsBody(String dnsBody) {
        try {
            if (!ToMySQL.exist(dnsBody)) {
                ToMySQL.bodyToMysql(dnsBody);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
