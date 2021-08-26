package com.yunlu.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunlu.utils.mysql.MySqlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.util.concurrent.*;

public class WriteUtil {
    private final static Logger LOGGER = LoggerFactory.getLogger(WriteUtil.class);

    public static void writeDB(String filePath, String baseDns, String tableName) {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    WriteUtil.cleanFile(filePath);
                    JSONArray list = MySqlUtil.getAllList(tableName);
                    for (Object l : list) {
                        JSONObject object = JSONObject.parseObject(String.valueOf(l));
                        if (!baseDns.equals(object.get("address"))) {
                            WriteUtil.write(String.valueOf(object.get("address")) + "\t"
                                    + String.valueOf(object.get("dnsin") + "\t")
                                    + String.valueOf(object.get("dnstype")) + "\t"
                                    + String.valueOf(object.get("ip")), filePath);
                        } else {
                            WriteUtil.write(String.valueOf(object.get("address")) + "\t"
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
    }

    public static synchronized void write(String file, String savePath) {
        try {
            FileWriter out = new FileWriter(savePath, true);
            out.write(String.valueOf(file));
            out.write("\r\n");
            out.flush();
            out.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public static synchronized void cleanFile(String savePath) {
        try {
            FileWriter fileWriter = new FileWriter(savePath, false);
            fileWriter.write(String.valueOf(""));
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
