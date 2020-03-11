package Dao.recycler;

import Dao.DaoUtils.ExeRes;
import Dao.DaoUtils.TableConfig;
import Dao.DaoUtils.TestDataUtil;
import Dao.MainDBDao;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunlu.core.data.sql.SqlBuilder;
import com.yunlu.core.data.sql.SqlCondition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;

import static Dao.DaoUtils.TableConfig.getProjectTableName;

@Component
public class TryDBwithJDBC {

    private Connection conn = null;
    static{
        System.out.println(1);
    }

    public static void main(String[] args){
//        String project = "company";
//        JSONObject jo = TestDataUtil.getProjectJsonObject(project);
////        TryDBwithJDBC tdbJDBC = new TryDBwithJDBC();
//        MainDBDao maindao = new MainDBDao();
////        ExeRes exeRes = tdbJDBC.addRecord(jo,project);
//        ExeRes exeRes;
//        exeRes = maindao.addRecord(jo,project);
////        exeRes = tdbJDBC.do_select(null,project,null);
////        jo = new JSONObject();
////        jo.put("name","孙睿智");
//        List<SqlCondition> condition_list = new ArrayList<>();
//        SqlCondition sc = new SqlCondition("name","really",SqlCondition.EQ);
//        condition_list.add(sc);
////        exeRes = tdbJDBC.do_update(jo,project,condition_list);
//
////        exeRes = tdbJDBC.do_delete(project,condition_list);
//
//        System.out.println(exeRes);
    }

    // 增加记录
    public ExeRes addRecord(JSONObject jo,String projectName){
        ExeRes exeres = new ExeRes();
        String tableName = "kg_covid_company";
        String[] companyFields = TableConfig.getProjectFields(projectName);
        ExeRes check_res = checkRecordDup(jo,tableName,companyFields);
        if (check_res.getExe_res()){
            if (check_res.getExe_info()!=null && StringUtils.isNotEmpty(check_res.getExe_info())){
                // 如果检查信息不为空 说明记录重复
                exeres.setExe_res(false);
                exeres.setExe_info(TableConfig.RECORD_DUP_MARK);
                return exeres;
            }
        } else {
            // 检查失败 报错返回
            check_res.setExe_info(TableConfig.RECORD_CHECK_FAILURE + ":"+check_res.getExe_info());
            return check_res;
        }
        // 如果检查信息为空或者为null 说明无此记录 可以插入
        ExeRes insert_res = do_insert(jo,projectName);
        exeres = insert_res;
        return exeres;
    }

    // 检查重复记录
    public ExeRes checkRecordDup(JSONObject j_o,String table_name,String[] company_fields){
        ExeRes exeres = new ExeRes(),db_res;
        db_res = getStatement();
        Statement stm;
        if (!db_res.getExe_res()){
            db_res.setExe_data(null);
            return db_res;
        } else {
            stm = (Statement) db_res.getExe_data();
        }
        SqlBuilder sb = new SqlBuilder();
        sb.append("select `id` from " + table_name);
        SqlBuilder tmp = new SqlBuilder();
        for (String field:company_fields){
            if (j_o.containsKey(field) && StringUtils.isNotEmpty(j_o.getString(field))){
//                tmp.append(" and ? = ? ").args(field,j_o.getString(field));
                tmp.append(String.format(" and `%s` = '%s' ",field,j_o.getString(field)));
            }
        }
        if (StringUtils.isNotEmpty(tmp.toString())){
            sb.append(" where 1=1 ");
            sb.append(tmp.toString());
        }
        String sql = sb.toString();
        try{
            ResultSet rs = stm.executeQuery(sql);
            while (rs.next()){
                exeres.setExe_info(rs.getString("id"));
            }
            exeres.setExe_res(true);
            rs.close();
        } catch (Exception e){
            exeres.setExe_res(false);
            exeres.setExe_info(e.toString());
        }
        return exeres;
    }

    // 查询
    public ExeRes do_select(List<SqlCondition> condition_list,String project,String[] select_fields){
        ExeRes exeres = new ExeRes(),db_res;
        String table_name = TableConfig.getProjectTableName(project);
        String[] fields = TableConfig.getProjectFields(project);
        SqlBuilder sb = new SqlBuilder(),fields_part = new SqlBuilder(),values_part = new SqlBuilder();
        db_res = getStatement();
        Statement stm;
        if (!db_res.getExe_res()){
            db_res.setExe_data(null);
            return db_res;
        } else {
            stm = (Statement) db_res.getExe_data();
        }
        sb.append("select ");
        if (select_fields == null || select_fields.length == 0){
            sb.append(" * ");
        } else {
            SqlBuilder tmp = new SqlBuilder();
            for (String field:select_fields){
                tmp.append(String.format("`%s`,",field));
            }
            String select_string = tmp.toString();
            sb.append(select_string.substring(0,select_string.length()-1));
        }
        sb.append(" from " + table_name);
        sb.append(generateWhereSql(condition_list));
        String sql = sb.toString() + ";";
        try {
            ResultSet rs = stm.executeQuery(sql);
            JSONArray ja = new JSONArray();
            JSONObject jo;
            String[] toSelectFields;
            if (sql.contains("*")){
                toSelectFields = fields;
            } else {
                toSelectFields = select_fields;
            }
            while (rs.next()){
                jo = new JSONObject();
                for (String field:toSelectFields){
                    jo.put(field,rs.getString(field));
                }
                ja.add(jo);
            }
            exeres.setExe_res(true);
            exeres.setExe_info(TableConfig.SELECT_SUCCESS);
            exeres.setExe_data(ja);
            rs.close();
        } catch (Exception e){
            exeres.setExe_res(false);
            exeres.setExe_info(TableConfig.SELECT_FAILURE);
        }
        return exeres;
    }

    // 插入记录
    public ExeRes do_insert(JSONObject j_o,String project){
        ExeRes exeres = new ExeRes(),db_res;
        String table_name = TableConfig.getProjectTableName(project);
        String[] company_fields = TableConfig.getProjectFields(project);
        if(table_name == null || company_fields == null){
            exeres.setExe_info(TableConfig.PROJECT_CONFIG_ERROR);
            exeres.setExe_res(false);
        }
        SqlBuilder sb = new SqlBuilder(),fields_part = new SqlBuilder(),values_part = new SqlBuilder();
        db_res = getStatement();
        Statement stm;
        if (!db_res.getExe_res()){
            db_res.setExe_data(null);
            return db_res;
        } else {
            stm = (Statement) db_res.getExe_data();
        }
        sb.append("insert into "+table_name);
        for (String field : company_fields){
            if (j_o.containsKey(field) && StringUtils.isNotEmpty(j_o.getString(field))){
                fields_part.append(String.format("`%s`",field));
                fields_part.append(",");
                values_part.append(String.format("'%s'",j_o.getString(field)));
                values_part.append(",");
            }
        }
        if (StringUtils.isEmpty(fields_part.toString()) || StringUtils.isEmpty(values_part.toString())){
            exeres.setExe_res(false);
            exeres.setExe_info(TableConfig.RECORD_INSERT_CONDITION_FAILURE);
        } else {
            String fields_string = fields_part.toString(),values_string = values_part.toString();

            String sql = sb.toString() + String.format(" (%s) values (%s);",fields_string.substring(0,fields_string.length()-1),values_string.substring(0,values_string.length()-1));
            try {
                Integer rows_affected = stm.executeUpdate(sql);
                exeres.setExe_res(true);
                exeres.setExe_info(TableConfig.RECORD_INSERT_SUCCESS);
                exeres.setExe_data(rows_affected);
            } catch (Exception e){
                exeres.setExe_res(false);
                exeres.setExe_info(TableConfig.RECORD_INSERT_FAILURE);
            }
        }
        return exeres;
    }

    // 更新记录
    public ExeRes do_update(JSONObject j_o,String project, List<SqlCondition> condition_list){
        ExeRes exeres = new ExeRes(),db_res;
        String table_name = TableConfig.getProjectTableName(project);
        db_res = getStatement();
        Statement stm;
        if (!db_res.getExe_res()){
            db_res.setExe_data(null);
            return db_res;
        } else {
            stm = (Statement) db_res.getExe_data();
        }
        SqlBuilder sb = new SqlBuilder();
        sb.append("update " + table_name + " set ");
        Set<String> key_set = j_o.keySet();
        if (key_set.size()==0){
            exeres.setExe_res(false);
            exeres.setExe_info(TableConfig.RECORD_UPDATE_CONDITION_FAILURE);
            return exeres;
        }
        SqlBuilder update_values = new SqlBuilder();
        for (String key:key_set){
            if (StringUtils.isNotEmpty(j_o.getString(key))){
                update_values.append(String.format(" `%s`='%s',",key,j_o.getString(key)));
            }
        }
        if (update_values.toString().length()==0){
            exeres.setExe_res(false);
            exeres.setExe_info(TableConfig.RECORD_UPDATE_CONDITION_FAILURE);
            return exeres;
        } else {
            String update_string = update_values.toString();
            sb.append(update_string.substring(0,update_string.length()-1));
        }
        sb.append(generateWhereSql(condition_list));
        String sql = sb.toString();
        try {
            Integer rows_affected = stm.executeUpdate(sql);
            exeres.setExe_res(true);
            exeres.setExe_info(TableConfig.RECORD_UPDATE_SUCCESS);
            exeres.setExe_data(rows_affected);
        } catch (Exception e){
            exeres.setExe_res(false);
            exeres.setExe_info(TableConfig.RECORD_UPDATE_FAILURE);
        }
        return exeres;
    }

    // 删除记录
    public  ExeRes do_delete(String project, List<SqlCondition> condition_list){
        ExeRes exeres = new ExeRes(),db_res;
        String table_name = TableConfig.getProjectTableName(project);
        db_res = getStatement();
        Statement stm;
        if (!db_res.getExe_res()){
            db_res.setExe_data(null);
            return db_res;
        } else {
            stm = (Statement) db_res.getExe_data();
        }
        SqlBuilder sb = new SqlBuilder();
        sb.append("delete from " + table_name);
        sb.append(generateWhereSql(condition_list));
        String sql = sb.toString();
        try {
            Integer rows_affected = stm.executeUpdate(sql);
            exeres.setExe_res(true);
            exeres.setExe_info(TableConfig.RECORD_DELETE_SUCCESS);
            exeres.setExe_data(rows_affected);
        } catch (Exception e){
            exeres.setExe_res(false);
            exeres.setExe_info(TableConfig.RECORD_DELETE_FAILURE);
        }
        return exeres;
    }

    // 生成where语句
    public String generateWhereSql(List<SqlCondition> condition_list){
        SqlBuilder condition_sb = new SqlBuilder();
        String whereSql = "";
        if (condition_list!=null && condition_list.size()>0){
            for (SqlCondition cond:condition_list){
                condition_sb.append(" and " + cond.toString());
            }
            if (condition_sb.toString().length()>0){
                whereSql = " where 1=1 " + condition_sb.toString();
            }
        }
        return whereSql;
    }

    // 获取数据库操作对象
    public ExeRes getStatement(){
        ExeRes exeRes = new ExeRes();
        exeRes.setExe_res(true);
        Statement statement = null;
        if (conn == null){
            // 驱动程序名
            String driver = "com.mysql.jdbc.Driver";
            // URL指向要访问的数据库名scutcs
            String url = "jdbc:mysql://106.74.152.45:19362/testcksdb?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull";
            // MySQL配置时的用户名
            String user = "mysql";
            // MySQL配置时的密码
            String password = "U2qafVuvDH";
            try {
                // 加载驱动程序
                Class.forName(driver);
                // 连续数据库
                conn = DriverManager.getConnection(url, user, password);
                if(!conn.isClosed()) {
                    System.out.println("Succeeded connecting to the Database!");
                }

            } catch(ClassNotFoundException e) {
                System.out.println("Sorry,can`t find the Driver!");
                e.printStackTrace();
                exeRes.setExe_res(false);
                exeRes.setExe_info(TableConfig.DB_CONNECT_FAILURE + "\t" + e.toString());
            } catch(Exception e) {
                e.printStackTrace();
                exeRes.setExe_res(false);
                exeRes.setExe_info(TableConfig.DB_CONNECT_FAILURE + "\t" + e.toString());
            }
        }
        if(!exeRes.getExe_res()){
            return exeRes;
        }
        try {
            statement = conn.createStatement();
            exeRes.setExe_data(statement);
            exeRes.setExe_res(true);
        } catch (Exception e){
            exeRes.setExe_res(false);
            exeRes.setExe_info(TableConfig.DB_CONNECT_FAILURE + "\t" + e.toString());
        }
        return exeRes;
    }

}
