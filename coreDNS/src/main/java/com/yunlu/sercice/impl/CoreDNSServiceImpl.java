package com.yunlu.sercice.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunlu.dao.CoreDNSDAO;
import com.yunlu.utils.WriteUtil;
import com.yunlu.utils.mysql.MySqlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.yunlu.sercice.CoreDNSService;
import org.springframework.stereotype.Component;


@Component
public class CoreDNSServiceImpl implements CoreDNSService {
    private static Logger LOGGER = LoggerFactory.getLogger(CoreDNSServiceImpl.class);

    @Override
    public boolean addcoreDNS(String coreDNS, String filePath, String corednsbody) {
        try {
            JSONObject coreInfo = new JSONObject();
            if (null != coreDNS && null != filePath) {
                String[] dnslist = coreDNS.split("\\s+");
                if (dnslist[0].contains(corednsbody)) {
                    coreInfo.put("address", dnslist[0]);
                } else {
                    coreInfo.put("address", dnslist[0] + corednsbody);
                }
                coreInfo.put("dnsin", dnslist[1]);
                coreInfo.put("dnstype", dnslist[2]);
                coreInfo.put("ip", dnslist[3]);
                if (CoreDNSDAO.adddns(coreInfo)) {
                    WriteUtil.writeDB(filePath, corednsbody);
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }

    @Override
    public boolean deletecoreDNS(String coreDNS, String filePath, String corednsbody) {
        try {
            JSONObject coreInfo = new JSONObject();
            if (null != coreDNS && null != filePath) {
                String[] dnslist = coreDNS.split("\\s+");
                if (dnslist[0].contains(corednsbody)) {
                    coreInfo.put("address", dnslist[0]);
                } else {
                    coreInfo.put("address", dnslist[0] + corednsbody);
                }
                if (CoreDNSDAO.deldns(coreInfo)) {
                    WriteUtil.writeDB(filePath, corednsbody);
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }

    @Override
    public boolean updatecoreDNS(String coreDNS, String filePath, String corednsbody) {
        try {
            JSONObject coreInfo = new JSONObject();
            if (null != coreDNS && null != filePath) {
                String[] dnslist = coreDNS.split("\\s+");
                if (dnslist[0].contains(corednsbody)) {
                    coreInfo.put("address", dnslist[0]);
                } else {
                    coreInfo.put("address", dnslist[0] + corednsbody);
                }
                coreInfo.put("dnsin", dnslist[1]);
                coreInfo.put("dnstype", dnslist[2]);
                coreInfo.put("ip", dnslist[3]);
                if (CoreDNSDAO.updatedns(coreInfo)) {
                    WriteUtil.writeDB(filePath, corednsbody);
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }

    @Override
    public JSONObject getcoreDNS(String address) {
        try {
            return CoreDNSDAO.getdns(address);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    @Override
    public JSONArray getListcoreDNS() {
        try {
            return CoreDNSDAO.getList();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }
}
