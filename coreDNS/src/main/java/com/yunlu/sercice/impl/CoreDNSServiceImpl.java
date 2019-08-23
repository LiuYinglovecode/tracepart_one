package com.yunlu.sercice.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunlu.dao.CoreDNSDAO;
import com.yunlu.utils.mysql.ToMySQL;
import com.yunlu.utils.writeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.yunlu.sercice.CoreDNSService;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;


@Component
public class CoreDNSServiceImpl implements CoreDNSService {
    private static Logger LOGGER = LoggerFactory.getLogger(CoreDNSServiceImpl.class);

    @Override
    public boolean coreDNS(String coreDNS, String filePath, String corednsbody) {
        try {
            JSONObject coreInfo = new JSONObject();
            if (null != coreDNS && null != filePath) {
                String[] dnslist = coreDNS.split("\\s+");
                if (dnslist[0].contains(corednsbody)) {
                    coreInfo.put("address", dnslist[0]);
                } else {
                    coreInfo.put("address", dnslist[0] + corednsbody);
                }
                coreInfo.put("address", dnslist[0]);
                coreInfo.put("dnsin", dnslist[1]);
                coreInfo.put("dnstype", dnslist[2]);
                coreInfo.put("ip", dnslist[3]);
                CoreDNSDAO.toMysql(coreInfo);
                ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
                singleThreadExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            writeUtil.cleanFile(filePath);
                            JSONArray list = ToMySQL.getAllList();
                            for (Object l : list) {
                                JSONObject object = JSONObject.parseObject(String.valueOf(l));
                                if (!corednsbody.equals(object.get("address"))) {
                                    writeUtil.write(String.valueOf(object.get("address")) + "\t"
                                            + String.valueOf(object.get("dnsin") + "\t")
                                            + String.valueOf(object.get("dnstype")) + "\t"
                                            + String.valueOf(object.get("ip")), filePath);
                                } else {
                                    writeUtil.write(String.valueOf(object.get("address")) + "\t"
                                            + String.valueOf(object.get("dnsin")) + "\t"
                                            + String.valueOf(object.get("dnstype")) + "\t"
                                            + String.valueOf(object.get("dns")) + "\t"
                                            + String.valueOf(object.get("robbmanes")) + "\t"
                                            + String.valueOf(object.get("time")) + "\t"
                                            + String.valueOf(object.get("dns7200")) + "\t"
                                            + String.valueOf(object.get("dns3600")) + "\t"
                                            + String.valueOf(object.get("dns1209600")) + "\t"
                                            + String.valueOf(object.get("dns36002")), filePath);
                                }
                            }
                            Thread.sleep(1000 * 2);
                        } catch (InterruptedException e) {
                            LOGGER.error(e.getMessage());
                        }
                    }
                });
                singleThreadExecutor.shutdown();
                return true;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }
}
