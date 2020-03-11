package Dao.DaoUtils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class RecordUtils {

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
