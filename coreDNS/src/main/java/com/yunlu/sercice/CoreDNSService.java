package com.yunlu.sercice;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface CoreDNSService {

    public boolean addcoreDNS(String coreDNS, String filePath, String corednsbody);

    public boolean deletecoreDNS(String coreDNS, String filePath, String corednsbody);

    public boolean updatecoreDNS(String coreDNS, String filePath, String corednsbody);

    public JSONObject getcoreDNS(String address);

    public JSONArray getListcoreDNS();

}
