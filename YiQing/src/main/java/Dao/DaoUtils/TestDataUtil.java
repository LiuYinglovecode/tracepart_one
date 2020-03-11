package Dao.DaoUtils;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

public class TestDataUtil {
    private static HashMap<String, JSONObject> project2jo;
    static {
        project2jo = new HashMap<>();
        JSONObject jo;

        jo = new JSONObject();
        jo.put("source_id","123");
        jo.put("category_id","1231123");
        jo.put("company_id","1231123123123");
        jo.put("name","张伟");
        jo.put("scale","source_id");
        jo.put("employess","source_id");
        jo.put("adress","source_id");
        jo.put("post_code","source_id");
        jo.put("info","source_id");
        jo.put("contacts","source_id");
        jo.put("tel","source_id");
        jo.put("fax","fax");
        jo.put("country","source_id");
        jo.put("province","source_id");
        jo.put("city","source_id");
        jo.put("type","source_id");
        jo.put("model","source_id");
        jo.put("industry","source_id");
        jo.put("product","source_id");
        jo.put("brand","source_id");
        jo.put("status","source_id");
        String project = "company";
        project2jo.put(project,jo);

    }

    public static JSONObject getProjectJsonObject(String project){
        JSONObject jo = null;
        if (project2jo.containsKey(project) && project2jo.get(project)!=null){
            jo = project2jo.get(project);
        }
        return jo;
    }
}
