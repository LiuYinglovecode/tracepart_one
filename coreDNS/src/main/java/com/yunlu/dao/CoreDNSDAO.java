package com.yunlu.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunlu.utils.mysql.MySqlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CoreDNSDAO {
    private static Logger LOGGER = LoggerFactory.getLogger(CoreDNSDAO.class);
    private static java.util.Map<String, String> Map = null;

    public static boolean adddns(JSONObject coreDNS) {
        try {
            Map = (java.util.Map) coreDNS;
            String address = Map.get("address");
            if (!MySqlUtil.exist(address)) {
                return MySqlUtil.coreDNSToMysql(Map);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }

    public static void dnsBody(String dnsBody) {
        try {
            if (!MySqlUtil.exist(dnsBody)) {
                MySqlUtil.bodyToMysql(dnsBody);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public static boolean deldns(JSONObject coreDNS) {
        try {
            String address = coreDNS.getString("address");
            if (MySqlUtil.exist(address)) {
                return MySqlUtil.deleteDNS(address);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }

    public static boolean updatedns(JSONObject coreDNS) {
        try {
            Map = (java.util.Map) coreDNS;
            String address = Map.get("address");
            if (MySqlUtil.exist(address)) {
                return MySqlUtil.updateDNS(Map, address);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }

    public static JSONObject getdns(String address) {
        try {
            if (MySqlUtil.exist(address)) {
                return MySqlUtil.getdns(address);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    public static JSONArray getList() {
        try {
            return MySqlUtil.getAllList();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }
}
