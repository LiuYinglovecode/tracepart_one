package com.yunlu.sercice.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunlu.dao.CoreDnsDAO;
import com.yunlu.utils.WriteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.yunlu.sercice.CoreDnsService;
import org.springframework.stereotype.Component;


@Component
public class CoreDnsServiceImpl implements CoreDnsService {
    private static Logger LOGGER = LoggerFactory.getLogger(CoreDnsServiceImpl.class);

    @Override
    public boolean addCoreDns(String baseDns, String dns, String filePath, String tableName) {
        try {
            JSONObject dnsInfo = new JSONObject();
            if (null != baseDns && null != dns && null != filePath && null != tableName) {
                String[] dnslist = dns.split("\\s+");
                if (dnslist[0].contains(baseDns)) {
                    dnsInfo.put("address", dnslist[0]);
                } else {
                    dnsInfo.put("address", dnslist[0] + "." + baseDns);
                }
                dnsInfo.put("dnsin", dnslist[1]);
                dnsInfo.put("dnstype", dnslist[2]);
                dnsInfo.put("ip", dnslist[3]);
                if (CoreDnsDAO.addDns(dnsInfo, tableName)) {
                    WriteUtil.writeDB(filePath, baseDns, tableName);
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }

    @Override
    public boolean deleteCoreDns(String baseDns, String dns, String filePath, String tableName) {
        try {
            JSONObject dnsInfo = new JSONObject();
            if (null != baseDns && null != dns && null != filePath && null != tableName) {
                String[] dnslist = dns.split("\\s+");
                if (dnslist[0].contains(baseDns)) {
                    dnsInfo.put("address", dnslist[0]);
                } else {
                    dnsInfo.put("address", dnslist[0] + "." + baseDns);
                }
                if (CoreDnsDAO.deleteDns(dnsInfo, tableName)) {
                    WriteUtil.writeDB(filePath, baseDns, tableName);
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }

    @Override
    public boolean updateCoreDns(String baseDns, String dns, String filePath, String tableName) {
        try {
            JSONObject dnsInfo = new JSONObject();
            if (null != baseDns && null != dns && null != filePath && null != tableName) {
                String[] dnslist = dns.split("\\s+");
                if (dnslist[0].contains(baseDns)) {
                    dnsInfo.put("address", dnslist[0]);
                } else {
                    dnsInfo.put("address", dnslist[0] + "." + baseDns);
                }
                dnsInfo.put("dnsin", dnslist[1]);
                dnsInfo.put("dnstype", dnslist[2]);
                dnsInfo.put("ip", dnslist[3]);
                if (CoreDnsDAO.updateDns(dnsInfo, tableName)) {
                    WriteUtil.writeDB(filePath, baseDns, tableName);
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }

    @Override
    public JSONObject getCoreDns(String address, String tableName) {
        try {
            return CoreDnsDAO.getDns(address, tableName);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    @Override
    public JSONArray getCoreDnsList(String tableName) {
        try {
            return CoreDnsDAO.getList(tableName);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }
}
