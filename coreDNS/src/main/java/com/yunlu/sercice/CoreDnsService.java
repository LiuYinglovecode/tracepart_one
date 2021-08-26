package com.yunlu.sercice;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author liyujie
 */
public interface CoreDnsService {

    public boolean addCoreDns(String baseDns, String dns, String filePath, String tableName);

    public boolean deleteCoreDns(String baseDns, String dns, String filePath, String tableName);

    public boolean updateCoreDns(String baseDns, String dns, String filePath, String tableName);

    public JSONObject getCoreDns(String address, String tableName);

    public JSONArray getCoreDnsList(String tableName);

}
