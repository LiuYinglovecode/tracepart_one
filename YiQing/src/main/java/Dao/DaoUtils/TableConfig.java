package Dao.DaoUtils;

import com.alibaba.fastjson.JSONObject;
import net.sf.json.JSONArray;

import java.util.*;

public class TableConfig {

    // 缺少项目对应配置
    public static String PROJECT_CONFIG_ERROR = "project_error";
    // 记录重复
    public static String RECORD_DUP_MARK = "record_repetition";
    // insert成功
    public static String RECORD_INSERT_SUCCESS = "insert_success";
    // insert失败
    public static String RECORD_INSERT_FAILURE = "insert_failure";
    // 检查记录失败
    public static String RECORD_CHECK_FAILURE = "check_failure";
    // 插入条件错误
    public static String RECORD_INSERT_CONDITION_FAILURE = "condition_error";
    // 数据库连接失败
    public static String DB_CONNECT_FAILURE = "db_disconnected";
    // 查询失败
    public static String SELECT_FAILURE = "select_failure";
    // 查询成功
    public static String SELECT_SUCCESS = "select_success";
    // 更新条件错误
    public static String RECORD_UPDATE_CONDITION_FAILURE = "condition_error";
    // 更新错误
    public static String RECORD_UPDATE_FAILURE = "update_failure";
    // 更新成功
    public static String RECORD_UPDATE_SUCCESS = "update_success";
    // 删除错误
    public static String RECORD_DELETE_FAILURE = "delete_failure";
    // 删除成功
    public static String RECORD_DELETE_SUCCESS = "delete_success";
    // 插入成功
    public static String RECORD_ADD_SUCCESS = "add_success";
    // 插入失败
    public static String RECORD_ADD_FAILURE = "add_failure";


    private static Map<String,String[]> project2fields;
    private static Map<String,String> project2tableName;
    private static Map<String,String> project2uniquefield;

    static {
        project2fields = new HashMap<>();
        project2tableName = new HashMap<>();
        project2uniquefield = new HashMap<>();
        String project;
        project = "company";
        project2fields.put(project,new String[]{"id","source_id","category_id","company_id","name","scale","employess","adress","post_code","info","contacts","tel","fax","country","province","city",
                "type","model","industry","product","brand","status","create_time","update_time","deleted"});
//        project2tableName.put(project,"kg_covid_company_test");
        project2tableName.put(project,"kg_covid_company");
        project2uniquefield.put(project,"name");


        // 抗疫数据源管理表
//        project="kg_covid_datasource";
        project="datasource";
        project2fields.put(project,new String[]{"id","name","url","source","status","publish_time","create_time","update_time","deleted"});
        project2tableName.put(project,"kg_covid_datasource");
        project2uniquefield.put(project,"name");

//        // 抗疫企业表
//        project="kg_covid_company";
//        project2fields.put(project,new String[]{"id","source_id","category_id","company_id","name","scale","employees","adress","post_code","info","contacts","tel","fax","country","province","city","type","model","industry","product","brand","status","create_time","update_time","deleted"});
//        project2tableName.put(project,"kg_covid_company");

        // 抗疫产品表
//        project="kg_covid_product";
        project="product";
        project2fields.put(project,new String[]{"id","source_id","category_id","company_id","contacts","tel","name","brand","specs","material","classify_name","level","charge_unit","price","inventory","moq","image","description","status","create_time","update_time","deleted"});
//        project2tableName.put(project,"kg_covid_product_test");
        project2tableName.put(project,"kg_covid_product");
        project2uniquefield.put(project,"name");

        // 产品类别表
        project="kg_covid_product_category";
        project2fields.put(project,new String[]{"id","name","key","material_category_ids","status","create_time","update_time","deleted"});
        project2tableName.put(project,"kg_covid_product_category");

        // 抗疫能力表
        project="kg_covid_tenant";
        project2fields.put(project,new String[]{"id","source_id","category_id","company_id","contacts","tel","classify_name","product_name","charge_unit","price","inventory","moq","image","description","status","create_time","update_time","deleted"});
        project2tableName.put(project,"kg_covid_tenant");

        // 原材料表
//        project="kg_covid_material";
        project="material";
        project2fields.put(project,new String[]{"id","source_id","category_id","company_name","company_id","contacts","tel","product_name","brand","specs","material","classify_name","level","charge_unit","price","inventory","moq","image","description","status","create_time","update_time","deleted"});
        project2tableName.put(project,"kg_covid_material");

        // 原材料类别表
        project="kg_covid_material_category";
        project2fields.put(project,new String[]{"id","product_category_id","name","key","status","create_time","update_time","deleted"});
        project2tableName.put(project,"kg_covid_material_category");

        // 疫情资讯表
        project="kg_covid_news";
        project2fields.put(project,new String[]{"id","source_id","title","subtitle","summary","content","type","status","create_time","update_time","deleted"});
        project2tableName.put(project,"kg_covid_news");

        // 医疗器械标准表
        project="kg_covid_standard";
        project="standard";
        project2fields.put(project,new String[]{"id","source_id","category_id","name","scope","language","description","file_url","file_size","file_format","status","create_time","update_time","deleted"});
        project2tableName.put(project,"kg_covid_standard");
        project2uniquefield.put(project,"code");

        // 医疗器械专利表
        project="kg_covid_patent";
        project="patent";
        project2fields.put(project,new String[]{"id","source_id","category_id","patent_num","name","url","summary","type","case_status","technical_field","trading_status","price","application_number","application_date","publication_number","publication_date","applicant","inventor","main_classification_number","classification_number","adress","country_code","agency","agent","preemption","assertion","instructions","file_url","file_size","file_format","law_info","law_status","summary_picture_url","instructions_picture_url","tfc_picture_url","status","create_time","update_time","deleted"});
        project2tableName.put(project,"kg_covid_patent");
        project2uniquefield.put(project,"patent_num");

    }

    // 获取表对应字段
    public static String[] getProjectFields(String project){
        String[] fields = null;
        if (project2fields.containsKey(project)){
            fields = project2fields.get(project);
        }
        return fields;
    }

    // 获取表对应表
    public static String getProjectTableName(String project){
        String table_name = null;
        if (project2tableName.containsKey(project)){
            table_name = project2tableName.get(project);
        }
        return table_name;
    }

    // 获取表唯一标志
    public static String getProjectUniqueFiedl(String project){
        String table_name = null;
        if (project2uniquefield.containsKey(project)){
            table_name = project2uniquefield.get(project);
        }
        return table_name;
    }

}
