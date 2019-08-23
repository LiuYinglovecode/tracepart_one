package com.yunlu.sercice.impl;

import com.alibaba.fastjson.JSONObject;
import com.yunlu.core.config.ConfigClient;
import com.yunlu.dao.CoreDNSDAO;
import com.yunlu.utils.mysql.ToMySQL;
import com.yunlu.utils.writeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.yunlu.sercice.CoreDNSService;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;


@Component
public class CoreDNSServiceImpl implements CoreDNSService {
    private static Logger LOGGER = LoggerFactory.getLogger(CoreDNSServiceImpl.class);
//    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean coreDNS(String coreDNS, String filePath, String corednsbody) {
        try {
            JSONObject coreInfo = new JSONObject();
//            String timestamp = sdf.format(new Date(Long.parseLong(String.valueOf(System.currentTimeMillis()))));
            if (null != coreDNS && null != filePath && null != corednsbody) {
                String[] dnslist = coreDNS.split("\\s+");
                coreInfo.put("address", dnslist[0]);
                coreInfo.put("dnsin", dnslist[1]);
                coreInfo.put("dnstype", dnslist[2]);
                coreInfo.put("ip", dnslist[3]);
//                coreInfo.put("time", sdf.format(new Date(Long.parseLong(String.valueOf(System.currentTimeMillis())))));
                CoreDNSDAO.toMysql(coreInfo);
                ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
                singleThreadExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List list = ToMySQL.getAllList();
                            writeUtil.cleanFile(filePath);
                            for (Object l : list) {
                                writeUtil.write(String.valueOf(l), filePath);
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
