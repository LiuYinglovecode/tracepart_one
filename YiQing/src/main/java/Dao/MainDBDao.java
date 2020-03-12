package Dao;

import Dao.DaoUtils.RecordUtils;
import Dao.DaoUtils.ExeRes;
import Dao.DaoUtils.TableConfig;
import Dao.YiqingDTO.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunlu.core.data.cache.impl.MemoryExpireCache;
import com.yunlu.core.data.sql.BaseDao;
import com.yunlu.core.data.sql.SqlBuilder;
import com.yunlu.core.data.sql.SqlCondition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Component
public class MainDBDao {
//    @Autowired
//    private TryDBwithJDBC dao_target;

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
//    @Autowired
//    private MemoryExpireCache cache;

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


    private MemoryExpireCache memory_cache = new MemoryExpireCache();
    private Long expire = (long)60*60*12*1000;
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

    private HashSet<String> product_set = null;
    private Boolean product_set_loaded = false;
    // 加载产品记录集合
    private void loadProductRecordSet(){
        if (!product_set_loaded || product_set==null){
            product_set = new HashSet<>();
            BaseDao product_dao = getDaoByProject("product");
            List<ProductDTO> product_list = product_dao.list("where deleted=0");
            String row = "";
            for (ProductDTO product_row : product_list){
                row = String.format("%s;%s;%s;%s",product_row.getSourceId(),product_row.getCategoryId(),product_row.getCompanyId(),product_row.getName());
                product_set.add(row);
            }
            product_set_loaded = true;
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
        String where_part = RecordUtils.convertJSONObject2String(project,jo);
        try {
            net.sf.json.JSONArray ja = net.sf.json.JSONArray.fromObject(target_dao.list(where_part));
            JSONArray ja_ali;
            if (ja.size()>0){
                ja = (net.sf.json.JSONArray) ja.getJSONObject(0).get("list");
//                JSONArray ja2;
//                ja2 = (JSONArray) JSONArray.toJSON(target_dao.list(where_part));
                ja_ali = RecordUtils.convertJA2JAAlibaba(ja);
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
        String[] values = RecordUtils.getValuesFromJSONObject(jo,names);
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
        String[] values = RecordUtils.getValuesFromJSONObject(jo,names);
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
//    public  ExeRes do_delete(String project, List<SqlCondition> condition_list){
//        return dao_target.do_delete(project,condition_list);
//    }

    // 增加产品记录
    public ExeRes addProductRecord(JSONObject jo){
        loadDao();
        /*
        检查jo中的公司信息、sourceid和categoryid
            三者都有且不为空，则获取对应的source_id、category_id、company_id
            三者缺一或者有为空的，返回报错
        通过companyname在缓存中查找信息
            如果有 则装配信息id、sourceid和categoryid
            如果无 则在数据库中查找
                如果有，则装配信息id、sourceid和categoryid
                无则构建公司信息（公司名） 插入并获取id
        装载集合
        在记录集合中查找是否有相同记录
            有则返回
            无则继续

        对比jo中的sourceid和categoryid与公司sourceid和categoryid是否有区别
            如果有 更新公司信息和cache信息
        插入产品记录
        插入集合

        */
//        return dao_target.addRecord(jo,projectName);
        ExeRes exeres = new ExeRes();

        // 检查jo中的公司信息、sourceid和categoryid
        ExeRes check_res = RecordUtils.checkKeyFieldsInJSONObject(jo,new String[]{"company_name"});
        if (!check_res.getExe_res()){
            return check_res;
        }
        // 通过公司名获取公司信息
        String company_id = "",company_sourceid = "",company_categoryid = "",company_name = jo.getString("company_name");
            // 首先通过缓存查找
        JSONObject company_info = (JSONObject) memory_cache.get(company_name);
        if (company_info == null){
            // 缓存中没有 则在数据库中查找
            BaseDao company_dao = getDaoByProject("company");
            SqlBuilder company_sb = new SqlBuilder();
            company_sb.append("where `name`=?").arg(company_name);
            CompanyDTO company_infoDTO = (CompanyDTO) company_dao.get(company_sb.toString(),null);
            int timer = 0;
            while (company_infoDTO == null){
                company_dao.insert(new String[]{"name","source_id"},new String[]{company_name,jo.getString("source_id")});
                company_infoDTO = (CompanyDTO) company_dao.get(company_sb.toString(),null);
                timer ++;
                if (timer > 10){
                    exeres.setExe_res(false);
                    exeres.setExe_info(TableConfig.RECORD_ADD_FAILURE + "\tfail to insert a new company record");
                }
            }
            company_id = company_infoDTO.getId();
            company_sourceid = company_infoDTO.getSourceId();
            company_categoryid = company_infoDTO.getCategoryId() != null ? company_infoDTO.getCategoryId() : "";
        } else {
            company_id = company_info.getString("company_id");
            company_sourceid = company_info.getString("source_id");
            company_categoryid = company_info.getString("category_id");
        }
        jo.put("company_id",company_id);
        jo.remove("company_name");


        // 装载集合 创建记录key 并 检查本次记录是否重复
        loadProductRecordSet();
        String product_key = String.format("%s;%s;%s;%s",jo.getString("source_id"),jo.getString("category_id"),jo.getString("company_id"),jo.getString("product_name"));
        if (product_set.contains(product_key)){
            exeres.setExe_res(true);
            exeres.setExe_info(TableConfig.RECORD_DUP_MARK);
            return exeres;
        }


        // 对比jo中的sourceid和categoryid与公司sourceid和categoryid是否有区别
        if (!company_sourceid.contains(jo.getString("source_id")) || !company_categoryid.contains(jo.getString("category_id"))){
            if (!company_sourceid.contains(jo.getString("source_id"))) {
                company_sourceid = StringUtils.isNotEmpty(company_sourceid) ? String.format("%s;%s", company_sourceid, jo.getString("source_id")) : jo.getString("source_id");
            }
            if (!company_categoryid.contains(jo.getString("category_id"))){
                company_categoryid = StringUtils.isNotEmpty(company_categoryid) ? String.format("%s;%s",company_categoryid,jo.getString("category_id")) : jo.getString("category_id");
            }
            SqlBuilder company_sb = new SqlBuilder();
            company_sb.append("where `id`=?").arg(company_id);
            CompanyDTO company_infoDTO = (CompanyDTO) company_dao.get(company_sb.toString(),null);
            company_infoDTO.setSourceId(company_sourceid);
            company_infoDTO.setCategoryId(company_categoryid);
            try {
                Boolean update_res = company_dao.update(company_infoDTO);
                if (!update_res){
                    throw new Exception();
                }
            } catch (Exception e){
                exeres.setExe_res(false);
                exeres.setExe_info("update_company_info_failure\t" + e);
                return exeres;
            }
            if (company_info == null){
                company_info = new JSONObject();
                company_info.put("compan_name",company_name);
                company_info.put("company_id",company_id);
            }
            // 更新缓存
            company_info.put("source_id",company_sourceid);
            company_info.put("category_id",company_categoryid);
            memory_cache.put(company_name,company_info,expire);
        }

        // 插入产品记录并加入集合
        String project = "product";
        BaseDao target_dao = getDaoByProject(project);
        try {
            exeres = do_insert(project,jo);
            if (!exeres.getExe_res()){
                return exeres;
            }
            product_set.add(product_key);
        } catch (Exception e){
            exeres.setExe_res(false);
            exeres.setExe_info(TableConfig.RECORD_INSERT_FAILURE);
        }
        return exeres;
    }


}
