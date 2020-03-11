package Dao;

import Dao.DaoUtils.ExeRes;
import Dao.DaoUtils.TableConfig;
import Dao.YiqingDTO.*;
import Dao.recycler.TryDBwithJDBC;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunlu.core.data.sql.BaseDao;
import com.yunlu.core.data.sql.SqlBuilder;
import com.yunlu.core.data.sql.SqlCondition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class MainDBDao {
    @Autowired
    private TryDBwithJDBC dao_target;

//    @Autowired
//    private DatasourceDao datasource_dao;
//    @Autowired
//    private CompanyDao company_dao;
//    @Autowired
//    private ProductDao product_dao = new ProductDao();
//    @Autowired
//    private ProductCategoryDao product_category_dao;
//    @Autowired
//    private TenantDao tenant_dao;
//    @Autowired
//    private MaterialDao material_dao;
//    @Autowired
//    private MaterialCategoryDao material_category_dao;
//    @Autowired
//    private NewsDao news_dao;
//    @Autowired
//    private StandardDao standard_dao;
//    @Autowired
//    private PatentDao patent_dao;

    private DatasourceDao datasource_dao = new DatasourceDao();
    private CompanyDao company_dao = new CompanyDao();
    private ProductDao product_dao = new ProductDao();
    private ProductCategoryDao product_category_dao = new ProductCategoryDao();
    private TenantDao tenant_dao = new TenantDao();
    private MaterialDao material_dao = new MaterialDao();
    private MaterialCategoryDao material_category_dao = new MaterialCategoryDao();
    private NewsDao news_dao = new NewsDao();
    private StandardDao standard_dao = new StandardDao();
    private PatentDao patent_dao = new PatentDao();

    private static HashMap<String, BaseDao> project2dao;
    private void loadDao(){
        if (project2dao == null){
            project2dao = new HashMap<>();
            project2dao.put("datasource",datasource_dao);
            project2dao.put("company",company_dao);
            project2dao.put("product",product_dao);
            project2dao.put("product_category",product_category_dao);
            project2dao.put("tenant",tenant_dao);
            project2dao.put("material",material_dao);
            project2dao.put("material_category",material_category_dao);
            project2dao.put("news",news_dao);
            project2dao.put("standard",standard_dao);
            project2dao.put("patent",patent_dao);
        }
    }

    public BaseDao getDaoByProject(String project){
        BaseDao target_dao = null;
        if (project2dao.containsKey(project)){
            target_dao = project2dao.get(project);
        }
        return target_dao;
    }


    // 查询
    public ExeRes do_select(String project,JSONObject jo, String[] fields2query){
//        return dao_target.do_select(condition_list,project,select_fields);
        ExeRes exeres = new ExeRes();
        loadDao();
        BaseDao target_dao = getDaoByProject(project);
        String where_part = convertJSONObject2String(project,jo);
        try {
            net.sf.json.JSONArray ja = net.sf.json.JSONArray.fromObject(target_dao.list(where_part));
            JSONArray ja_ali;
            if (ja.size()>0){
                ja = (net.sf.json.JSONArray) ja.getJSONObject(0).get("list");
//                JSONArray ja2;
//                ja2 = (JSONArray) JSONArray.toJSON(target_dao.list(where_part));
                ja_ali = TableConfig.convertJA2JAAlibaba(ja);
            } else {
                ja_ali = new JSONArray();
            }
            exeres.setExe_res(true);
            exeres.setExe_info(TableConfig.SELECT_SUCCESS);
            exeres.setExe_data(ja_ali);
        } catch (Exception e){
            exeres.setExe_res(false);
            exeres.setExe_info(TableConfig.SELECT_FAILURE);
        }
        return exeres;
    }

    // 插入记录
    public ExeRes do_insert(String project,JSONObject jo){
//        return dao_target.do_insert(jo,project);
        ExeRes exeres = new ExeRes();
        Long cnt;
        loadDao();
        BaseDao target_dao = getDaoByProject(project);
//        cnt = target_dao.insert(jo);
        String[] names = jo.keySet().toArray(new String[jo.keySet().size()]);
        String[] values = TableConfig.getValuesFromJSONObject(jo,names);
        try{
            cnt = target_dao.insert(names,values);
            if (cnt>0){
                exeres.setExe_res(true);
                exeres.setExe_info(TableConfig.RECORD_INSERT_SUCCESS);
                exeres.setExe_data(cnt);
            } else {
                exeres.setExe_res(false);
                exeres.setExe_info(TableConfig.RECORD_INSERT_FAILURE);
                exeres.setExe_data(cnt);
            }
        } catch (Exception e){
            exeres.setExe_res(false);
            exeres.setExe_info(TableConfig.RECORD_INSERT_FAILURE);
        }

        return exeres;
    }

    // 更新记录
    public ExeRes do_update(String project,JSONObject jo, List<SqlCondition> condition_list){
//        return dao_target.do_update(j_o,project,condition_list);
        ExeRes exeres = new ExeRes();
        Long cnt;
        loadDao();
        BaseDao target_dao = getDaoByProject(project);
        String[] names = jo.keySet().toArray(new String[jo.keySet().size()]);
        String[] values = TableConfig.getValuesFromJSONObject(jo,names);
        try{
            Boolean res = target_dao.update(names,values,condition_list.toArray(new SqlCondition[condition_list.size()]));
            if (res){
                exeres.setExe_res(true);
                exeres.setExe_info(TableConfig.RECORD_UPDATE_SUCCESS);
                exeres.setExe_data(res);
            } else {
                exeres.setExe_res(false);
                exeres.setExe_info(TableConfig.RECORD_UPDATE_FAILURE);
            }
        } catch(Exception e){
            exeres.setExe_res(false);
            exeres.setExe_info(TableConfig.RECORD_UPDATE_FAILURE);
        }
        return exeres;
    }

    // 删除记录
    public  ExeRes do_delete(String project, List<SqlCondition> condition_list){
        return dao_target.do_delete(project,condition_list);
    }

    // 增加记录
    public ExeRes addRecord(String project,JSONObject jo){
//        return dao_target.addRecord(jo,projectName);
        ExeRes exeres = new ExeRes();
        loadDao();
        BaseDao target_dao = getDaoByProject(project);
        // List类型不能放进去 数组类型可以
//        List<SqlCondition> test1 = new ArrayList<>();
        SqlCondition[] where_part = convertJSONObject2SqlConditions(project,jo);
        Integer cnt = target_dao.count(where_part);
        if (cnt != null && cnt>0){
            exeres.setExe_res(false);
            exeres.setExe_info(TableConfig.RECORD_DUP_MARK);
            exeres.setExe_data(cnt);
        } else {
            exeres = do_insert(project,jo);
        }
        return exeres;
    }

    // JSONObject 转为 SqlCondition
    public SqlCondition[] convertJSONObject2SqlConditions(String project, JSONObject jo){
        List<SqlCondition> tmp = new ArrayList<>();
        SqlCondition sc;
        for (String key : jo.keySet()){
            sc = new SqlCondition(key,jo.getString(key),SqlCondition.EQ);
            tmp.add(sc);
        }
        return tmp.toArray(new SqlCondition[tmp.size()]);
    }

    // JSONObject 转为 where语句
    public String convertJSONObject2String(String project, JSONObject jo){
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
}
