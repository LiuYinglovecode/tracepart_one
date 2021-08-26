package com.yunlu.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunlu.utils.mysql.MySqlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CoreDnsDAO {
    private static Logger LOGGER = LoggerFactory.getLogger(CoreDnsDAO.class);
    private static java.util.Map<String, String> Map = null;

    public static boolean addDns(JSONObject dnsInfo, String tableName) {
        try {
            Map = (java.util.Map) dnsInfo;
            String address = Map.get("address");
            if (!MySqlUtil.exist(address, tableName)) {
                return MySqlUtil.addDns(Map, tableName);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }

    public static void initDns(String baseDns, String tableName) {
        try {
            if (!MySqlUtil.exist(baseDns, tableName)) {
                MySqlUtil.addBaseDns(baseDns, tableName);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public static boolean deleteDns(JSONObject dnsInfo, String tableName) {
        try {
            String address = dnsInfo.getString("address");
            if (MySqlUtil.exist(address, tableName)) {
                return MySqlUtil.deleteDns(address, tableName);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }

    public static boolean updateDns(JSONObject dnsInfo, String tableName) {
        try {
            Map = (java.util.Map) dnsInfo;
            String address = Map.get("address");
            if (MySqlUtil.exist(address, tableName)) {
                return MySqlUtil.updateDns(Map, address, tableName);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }

    public static JSONObject getDns(String address, String tableName) {
        try {
            if (MySqlUtil.exist(address, tableName)) {
                return MySqlUtil.getDns(address, tableName);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    public static JSONArray getList(String tableName) {
        try {
            return MySqlUtil.getAllList(tableName);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }
}
