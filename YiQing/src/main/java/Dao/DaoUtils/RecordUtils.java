package Dao.DaoUtils;

import com.alibaba.fastjson.JSONObject;
import com.yunlu.core.data.sql.SqlBuilder;
import com.yunlu.core.data.sql.SqlCondition;
import net.sf.json.JSONArray;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RecordUtils {

    // 将一种JSONArray 转为另一种 JSONArray(alibaba的)
    public static com.alibaba.fastjson.JSONArray convertJA2JAAlibaba(JSONArray ja){
        com.alibaba.fastjson.JSONArray valueArray = new com.alibaba.fastjson.JSONArray();
        net.sf.json.JSONObject jo;
        JSONObject valueRow;
        for (int i=0;i<ja.size();i++){
            jo = ja.getJSONObject(i);
            valueRow = new JSONObject();
            Set<String> keys = jo.keySet();
            for (String key : keys){
                valueRow.put(key,jo.get(key));
            }
            valueArray.add(jo);
        }
        return valueArray;
    }

    // 从JSONObject中返回值数组
    public static String[] getValuesFromJSONObject(JSONObject jo,String[] names){
        List<String> values_array = new ArrayList<>();
        for (String name : names){
            values_array.add(jo.getString(name));
        }
        return values_array.toArray(new String[values_array.size()]);
    }

    // JSONObject 转为 SqlCondition
    public static SqlCondition[] convertJSONObject2SqlConditions(String project, JSONObject jo){
        List<SqlCondition> tmp = new ArrayList<>();
        SqlCondition sc;
        for (String key : jo.keySet()){
            sc = new SqlCondition(key,jo.getString(key),SqlCondition.EQ);
            tmp.add(sc);
        }
        return tmp.toArray(new SqlCondition[tmp.size()]);
    }

    // JSONObject 转为 where语句
    public static String convertJSONObject2String(String project, JSONObject jo){
        SqlBuilder sb = new SqlBuilder();
        String out;
        for (String key : jo.keySet()){
            if (StringUtils.isNotEmpty(jo.getString(key))){
                sb.append(String.format(" and `%s`='%s'",key,jo.getString(key)));
            }
        }
        if (sb.toString().length()>0){
            out = " where 1=1 " + sb.toString();
        } else {
            out = "";
        }
        return out;
    }

    // 检查sourceid和categoryid 以及其他可选字段
    public static ExeRes checkKeyFieldsInJSONObject(JSONObject jo,String[] append_fields){
        ExeRes check_res = new ExeRes();
        List<String> check_fields = new ArrayList<>();
        for (String field : append_fields){
            check_fields.add(field);
        }
        check_fields.add("source_id");
        check_fields.add("category_id");
        check_res.setExe_res(true);
        check_res.setExe_info("");
        for (String field:check_fields){
            if (!jo.containsKey(field) || StringUtils.isEmpty(field)){
                check_res.setExe_res(false);
                check_res.setExe_info(check_res.getExe_info() + String.format("\t%s is necessary",field));
            }
        }
        return check_res;
    }
}
