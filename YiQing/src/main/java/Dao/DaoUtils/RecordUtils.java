package Dao.DaoUtils;

import Dao.YiqingDTO.CompanyDTO;
import Dao.YiqingDTO.MaterialDTO;
import Dao.YiqingDTO.PatentDTO;
import Dao.YiqingDTO.StandardDTO;
import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.mysqlx.protobuf.MysqlxExpr;
import com.yunlu.core.data.cache.impl.MemoryExpireCache;
import com.yunlu.core.data.sql.BaseDao;
import com.yunlu.core.data.sql.SqlBuilder;
import com.yunlu.core.data.sql.SqlCondition;
import net.sf.json.JSONArray;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
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
        check_fields.add("source_id");
        check_fields.add("category_id");
        for (String field : append_fields){
            check_fields.add(field);
        }

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

    public static Object baseConvert(JSONObject jo,Class dto_class){
//        MaterialDTO material_info = new MaterialDTO();
        Object dto = null;
        try{
            dto = dto_class.newInstance();
        } catch ( Exception e){
            return null;
        }
//        Class<?> dto = MaterialDTO.class;
        Method method;
        String method_name;
        for(String key : jo.keySet()){
            if (StringUtils.isNotEmpty(jo.getString(key))){
                method_name = getMethodName(key,"set");
                try {
                    method = dto_class.getMethod(method_name, String.class);
                    method.invoke(dto,jo.getString(key));
                } catch (Exception e){
                    System.out.println("there is no " + method_name);
                }
            }
        }
        return dto;
    }

    // 基于反射的比较与装填
    public static void compareAndAddList(Object target_info,JSONObject jo,List<String> names,List<String> values, Class dto_class,String project,String[] ignore_list){
        Method method_get,method_set;
        String method_name_get,method_name_set;
        HashSet<String> ignore_set = null;
        if (ignore_list!=null && ignore_list.length>0){
            ignore_set = new HashSet<>();
            for (String key:ignore_list){
                ignore_set.add(key);
            }
        }
        for (String key:jo.keySet()){
            if (ignore_set!=null && ignore_set.contains(key)){
                continue;
            }
            if (StringUtils.isNotEmpty(jo.getString(key))){
                method_name_get = getMethodName(key,"get");
                method_name_set = getMethodName(key,"set");
                try {
                    method_get = dto_class.getMethod(method_name_get);
                    method_set = dto_class.getMethod(method_name_set, String.class);
                    if ((method_get.invoke(target_info)==null || StringUtils.isEmpty((String)method_get.invoke(target_info))) && StringUtils.isNotEmpty(jo.getString(key))){
                        method_set.invoke(target_info,jo.getString(key));
                        names.add(key);
                        values.add(jo.getString(key));
                    }
                } catch (Exception e){
                    System.out.println("there is no " + method_name_get + " and " + method_name_set);
                }
            }
        }
    }

    // 更新
    public static ExeRes updateRecord(MemoryExpireCache memory_cache,BaseDao target_dao, Object target_info,List<String> names,List<String> values, Class dto_class,String project,Long expire,String[] ignore_list){
        ExeRes exeres = new ExeRes();
        Method method;
        if (names.size()>0 && values.size()>0 && names.size()==values.size()){
            List<SqlCondition> sq_list = new ArrayList<>();
//            sq_list.add(new SqlCondition("name",patent_info.getName(),SqlCondition.EQ));
            try{
                method = dto_class.getMethod("getId");
                String id = (String)method.invoke(target_info);
                sq_list.add(new SqlCondition("id",id,SqlCondition.EQ));
            } catch (Exception e){
                exeres.setExe_res(false);
                exeres.setExe_info("there is not getId");
                return exeres;
            }
            sq_list.add(new SqlCondition("deleted","0",SqlCondition.EQ));
            try{
                Boolean res = target_dao.update(names.toArray(new String[names.size()]),values.toArray(new String[values.size()]),sq_list.toArray(new SqlCondition[sq_list.size()]));
                try {
                    String unique_field = TableConfig.getProjectUniqueFiedl(project);
                    method = dto_class.getMethod(getMethodName(unique_field,"get"));
                    String name = (String)method.invoke(target_info);
                    memory_cache.put(project+name,target_info,expire);
                    exeres.setExe_res(true);
                    exeres.setExe_info(TableConfig.RECORD_UPDATE_SUCCESS);
                    exeres.setExe_data(res);
                }catch (Exception e){
                    exeres.setExe_res(false);
                    exeres.setExe_info("there is not getName");
                    return exeres;
                }
            } catch (Exception e){
                exeres.setExe_res(false);
                exeres.setExe_info(TableConfig.RECORD_UPDATE_FAILURE);
            }
        } else {
            exeres.setExe_res(true);
            exeres.setExe_info(TableConfig.RECORD_DUP_MARK);
        }
        return exeres;
    }

    // 插入
    public static ExeRes insertRecord(MemoryExpireCache memory_cache,BaseDao target_dao, JSONObject jo,Class dto_class,String project,Long expire,String[] ignore_list){
        ExeRes exeres = new ExeRes();
        Object target_info = null;
        Method method;
        try{
            target_info = baseConvert(jo,dto_class);
        } catch (Exception e){
            exeres.setExe_res(false);
            exeres.setExe_info("unexist_class");
            return exeres;
        }
        if (target_info == null){
            exeres.setExe_res(false);
            exeres.setExe_info("unexist_class");
            return exeres;
        }
        List<String> names = new ArrayList<>(),values = new ArrayList<>();
        loadNamesAndValuesFromJSONObject(jo,names,values);
        if (names.size()>0 && values.size()>0 && names.size() == values.size()){
            try {
                Long res = target_dao.insert(names.toArray(new String[names.size()]),values.toArray(new String[values.size()]));
                String unique_field = TableConfig.getProjectUniqueFiedl(project);
                method = dto_class.getMethod(getMethodName(unique_field,"get"));
                String name = (String)method.invoke(target_info);
                method = dto_class.getMethod("getId");
                if (StringUtils.isEmpty(((String)method.invoke(target_info)))){
                    method = dto_class.getMethod("setId",String.class);
                    method.invoke(target_info,res.toString());
                }
                memory_cache.put(project+name,target_info,expire);
                exeres.setExe_res(true);
                exeres.setExe_info(TableConfig.RECORD_INSERT_SUCCESS);
                exeres.setExe_data(res);
            } catch (Exception e){
                exeres.setExe_res(false);
                exeres.setExe_info(TableConfig.RECORD_INSERT_FAILURE);
            }
        }
        return exeres;
    }

    // 用于获得驼峰式的方法名
    public static String getMethodName(String property,String prefix){
        String method_name = prefix;
        String[] tmp = property.split("_");
        for (String part:tmp){
            method_name += part.substring(0,1).toUpperCase()+part.substring(1);
        }
        return method_name;
    }

    // 用于从JSONObject中获取信息 装载names和values
    public static void loadNamesAndValuesFromJSONObject(JSONObject jo,List<String> names,List<String> values){
        for (String key:jo.keySet()){
            if (StringUtils.isNotEmpty(jo.getString(key))){
                names.add(key);
//                String v = jo.getString(key);
                values.add(jo.getString(key));
            }
        }
    }

    // 输出错误的json
    public static void writeJSONtoFile(JSONObject jo){
        try{
            String content = String.format("%s\n",jo.toJSONString());
//            content = "13123\n";
            String filePath = "";
            File file =new File("D:\\工作文档\\公司编程项目集合\\企业驾驶舱\\2002报平安\\wrong_json_200316.txt");

            if(!file.exists()){
                file.createNewFile();
            }

            //使用true，即进行append file

            FileWriter fileWritter = new FileWriter(file.getName(),true);

//
//            fileWritter.write(content);
//
//            fileWritter.close();

//            System.out.println("finish");
            BufferedWriter writer = new BufferedWriter(fileWritter);
            writer.write(content);
            writer.close();
            fileWritter.close();

        }catch(IOException e){

            e.printStackTrace();

        }
    }
}
