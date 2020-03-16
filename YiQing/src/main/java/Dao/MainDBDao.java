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

    // 加载公司记录
    private static MemoryExpireCache memory_cache = new MemoryExpireCache();
    private static Long expire = (long)60*60*12*1000;
    private static Boolean company_cache_loaded = false;
    private void loadCompanyCache(){
        if (!company_cache_loaded){
            JSONObject jo;
            String companyName;
            String project = "company";
            BaseDao company_dao = getDaoByProject(project);
            List<CompanyDTO> companyList = company_dao.list("where deleted=0");
            for (CompanyDTO company:companyList){
                companyName = company.getName();
                memory_cache.put(project+companyName,company,expire);
            }
            company_cache_loaded = true;
        }
    }


    // 加载产品缓存
    private static Boolean product_cache_loaded = false;
    private void loadProductCache(){
        if (!product_cache_loaded){
            String project = "product";
            BaseDao product_dao = getDaoByProject(project);
            List<ProductDTO> product_list = product_dao.list("where deleted=0");
            for(ProductDTO product_row:product_list){
                memory_cache.put(project + product_row.getName(),product_row,expire);
            }
            product_cache_loaded = true;
        }
    }

    private static Boolean material_cache_loaded = false;
    private void loadMaterialCache(){
        if (!material_cache_loaded){
            String project = "material";
            BaseDao<MaterialDTO> materialDao = getDaoByProject(project);
            List<MaterialDTO> materialList = materialDao.list("where deleted=0");
            for(MaterialDTO materialRow:materialList){
                memory_cache.put(project + materialRow.getName(),materialRow,expire);
            }
            material_cache_loaded = true;
        }
    }

    private static Boolean patent_cache_loaded = false;
    private void loadPatentCache(){
        if (!patent_cache_loaded){
            String project = "patent";
            BaseDao patentDao = getDaoByProject(project);
            List<PatentDTO> patentList = patentDao.list("where deleted=0");
            for(PatentDTO patentRow:patentList){
                memory_cache.put(project + patentRow.getPatentNum(),patentRow,expire);
            }
            patent_cache_loaded = true;
        }
    }

    private static Boolean standard_cache_loaded = false;
    private void loadStandardCache(){
        if (!standard_cache_loaded){
            String project = "standard";
            BaseDao standardDao = getDaoByProject(project);
            List<StandardDTO> standardList = standardDao.list("where deleted=0");
            for(StandardDTO standardRow:standardList){
                memory_cache.put(project + standardRow.getCode(),standardRow,expire);
            }
            standard_cache_loaded = true;
        }
    }


    // 查询
    public ExeRes doSelect(String project,JSONObject jo, String[] fields2query){
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
    public ExeRes doInsert(String project,JSONObject jo){
        ExeRes exeres = new ExeRes();
        Long cnt;
        loadDao();
        BaseDao target_dao = getDaoByProject(project);
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
    public ExeRes doUpdate(String project,JSONObject jo, List<SqlCondition> condition_list){
        ExeRes exeres = new ExeRes();
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

    // 增加产品记录
    public ExeRes addProductRecord(JSONObject jo){
        loadDao();
        loadCompanyCache();
        loadProductCache();
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
        ExeRes exeRes = new ExeRes();
        String project = "product";
        // 检查jo中的公司信息 name source_id和category_id
        ExeRes check_res = RecordUtils.checkKeyFieldsInJSONObject(jo,new String[]{"name","company_name"});
        if (!check_res.getExe_res()){
            return check_res;
        }
        // 检查公司表中是否有同一家公司 首先检查缓存
        JSONObject company_jo = new JSONObject();
        company_jo.put("source_id",jo.getString("source_id"));
        company_jo.put("category_id",jo.getString("category_id"));
        company_jo.put("name",jo.getString("company_name"));
        ExeRes company_res = addCompanyRecord(company_jo);
        if (!company_res.getExe_res() || company_res.getExe_data()==null){
            company_res.setExe_res(false);
            return company_res;
        }
        CompanyDTO company_info = (CompanyDTO)company_res.getExe_data();
        jo.put("company_id",company_info.getId());
        jo.remove("company_name");
        ProductDTO product_info = (ProductDTO)memory_cache.get(project+jo.getString("name"));
        if (product_info==null){
            // 缓存中没有 则查找数据库
            BaseDao product_dao = getDaoByProject(project);
            SqlBuilder sb = new SqlBuilder();
            sb.append(" where `name`=? and deleted=0").arg(jo.getString("name"));
            product_info = (ProductDTO)product_dao.get(sb.toString());
        }
        if (product_info == null){
            exeRes = RecordUtils.insertRecord(memory_cache,product_dao,jo,ProductDTO.class,project,expire,null);
        } else {
            List<String> names = new ArrayList<>(),values = new ArrayList<>();

            if((product_info.getSourceId() == null || StringUtils.isEmpty(product_info.getSourceId())) && StringUtils.isNotEmpty(jo.getString("source_id"))){
                if (!product_info.getSourceId().contains(jo.getString("source_id"))){
                    product_info.setSourceId(jo.getString("source_id"));
                    names.add("source_id");
                    values.add(jo.getString("source_id"));
                }
            }
            if((product_info.getCategoryId() == null || StringUtils.isEmpty(product_info.getCategoryId())) && StringUtils.isNotEmpty(jo.getString("category_id"))){
                if (!product_info.getCategoryId().contains(jo.getString("category_id"))){
                    product_info.setCategoryId(jo.getString("category_id"));
                    names.add("category_id");
                    values.add(jo.getString("category_id"));
                }
            }
            RecordUtils.compareAndAddList(product_info,jo,names,values,ProductDTO.class,project,null);
            exeRes = RecordUtils.updateRecord(memory_cache,product_dao,product_info,names,values,ProductDTO.class,project,expire,null);
        }
        return exeRes;
    }


    // 增加公司信息
    public ExeRes addCompanyRecord(JSONObject jo){
        loadDao();
        /*
        检查必须字段 sourceid和categoryid
        检查缓存和数据库是否有记录
            如果都没有 则 根据jo创建insert信息和DTO，分别插入数据库和缓存
            如果有 则对比DTO和jo，选择是否更新dto和数据库

        */
        ExeRes exeRes = new ExeRes();
        // 检查jo中的公司信息 name source_id和category_id
        ExeRes check_res = RecordUtils.checkKeyFieldsInJSONObject(jo,new String[]{"name"});
        if (!check_res.getExe_res()){
            return check_res;
        }
        // 检查公司表中是否有同一家公司 首先检查缓存
        loadCompanyCache();
        CompanyDTO company_info = (CompanyDTO)memory_cache.get("company"+jo.getString("name"));
        if (company_info==null){
            // 缓存中没有 则查找数据库
            BaseDao company_dao = getDaoByProject("company");
            SqlBuilder sb = new SqlBuilder();
            sb.append(" where `name`=? and deleted=0").arg(jo.getString("name"));
            company_info = (CompanyDTO)company_dao.get(sb.toString());
        }
        if (company_info == null){
            exeRes = RecordUtils.insertRecord(memory_cache,company_dao,jo,CompanyDTO.class,"company",expire,null);
        } else {
            List<String> names = new ArrayList<>(),values = new ArrayList<>();
            if((company_info.getSourceId() == null || StringUtils.isEmpty(company_info.getSourceId())) && StringUtils.isNotEmpty(jo.getString("source_id"))){
                if (!company_info.getSourceId().contains(jo.getString("source_id"))){
                    company_info.setSourceId(String.format("%s,%s",company_info.getSourceId(),jo.getString("source_id")));
                    names.add("source_id");
                    values.add(String.format("%s,%s",company_info.getSourceId(),jo.getString("source_id")));
                }
            }
            if((company_info.getCategoryId() == null || StringUtils.isEmpty(company_info.getCategoryId())) && StringUtils.isNotEmpty(jo.getString("category_id"))){
                if (!company_info.getCategoryId().contains(jo.getString("category_id"))){
                    company_info.setCategoryId(String.format("%s,%s",company_info.getCategoryId(),jo.getString("category_id")));
                    names.add("category_id");
                    values.add(String.format("%s,%s",company_info.getSourceId(),jo.getString("category_id")));
                }
            }
            RecordUtils.compareAndAddList(company_info,jo,names,values,CompanyDTO.class,"company",null);
            exeRes = RecordUtils.updateRecord(memory_cache,company_dao,company_info,names,values,CompanyDTO.class,"company",expire,null);
        }
        exeRes.setExe_data((CompanyDTO)memory_cache.get("company"+jo.getString("name")));
        return exeRes;
    }

    // 增加原材料信息
    public ExeRes addMaterialRecord(JSONObject jo){
        loadDao();
        loadCompanyCache();
        loadMaterialCache();
        String project = "material";
        /*
        检查必须字段 sourceid和categoryid
        检查缓存和数据库是否有记录
            如果都没有 则 根据jo创建insert信息和DTO，分别插入数据库和缓存
            如果有 则对比DTO和jo，选择是否更新dto和数据库

        */
        ExeRes exeRes = new ExeRes();
        // 检查jo中的公司信息 name source_id和category_id
        ExeRes check_res = RecordUtils.checkKeyFieldsInJSONObject(jo,new String[]{"name","company_name"});
        if (!check_res.getExe_res()){
            return check_res;
        }
        // 检查公司表中是否有同一家公司 首先检查缓存
        JSONObject company_jo = new JSONObject();
        company_jo.put("source_id",jo.getString("source_id"));
        company_jo.put("category_id",jo.getString("category_id"));
        company_jo.put("name",jo.getString("company_name"));
        ExeRes company_res = addCompanyRecord(company_jo);
        if (!company_res.getExe_res() || company_res.getExe_data()==null){
            company_res.setExe_res(false);
            return company_res;
        }
        CompanyDTO company_info = (CompanyDTO)exeRes.getExe_data();
        jo.put("company_id",company_info.getId());
        jo.remove("company_name");
        MaterialDTO material_info = (MaterialDTO)memory_cache.get(project+jo.getString("name"));
        if (material_info==null){
            // 缓存中没有 则查找数据库
            BaseDao material_dao = getDaoByProject("company");
            SqlBuilder sb = new SqlBuilder();
            sb.append(" where `name`=? and deleted=0").arg(jo.getString("name"));
            material_info = (MaterialDTO)material_dao.get(sb.toString());
        }
        if (material_info == null){
            exeRes = RecordUtils.insertRecord(memory_cache,material_dao,jo,MaterialDTO.class,project,expire,null);
        } else {
            List<String> names = new ArrayList<>(),values = new ArrayList<>();

            if((material_info.getSourceId() == null || StringUtils.isEmpty(material_info.getSourceId())) && StringUtils.isNotEmpty(jo.getString("source_id"))){
                if (!material_info.getSourceId().contains(jo.getString("source_id"))){
                    material_info.setSourceId(jo.getString("source_id"));
                    names.add("source_id");
                    values.add(jo.getString("source_id"));
                }
            }
            if((material_info.getCategoryId() == null || StringUtils.isEmpty(material_info.getCategoryId())) && StringUtils.isNotEmpty(jo.getString("category_id"))){
                if (!material_info.getCategoryId().contains(jo.getString("category_id"))){
                    material_info.setCategoryId(jo.getString("category_id"));
                    names.add("category_id");
                    values.add(jo.getString("category_id"));
                }
            }
            RecordUtils.compareAndAddList(material_info,jo,names,values,MaterialDTO.class,project,null);
            exeRes = RecordUtils.updateRecord(memory_cache,material_dao,material_info,names,values,MaterialDTO.class,project,expire,null);
        }
        return exeRes;
    }

    // 增加专利信息
    public ExeRes addPatentRecord(JSONObject jo){
        loadDao();
        loadPatentCache();
        ExeRes exeres = new ExeRes();
        // 检查jo中的专利信息、sourceid和categoryid
        ExeRes check_res = RecordUtils.checkKeyFieldsInJSONObject(jo,new String[]{"patent_num","name"});
        if (!check_res.getExe_res()){
            return check_res;
        }

        PatentDTO patent_info = (PatentDTO)memory_cache.get(jo.getString("patent_num"));
        BaseDao patent_dao = getDaoByProject("patent");
        if (patent_info == null){
            SqlBuilder sb = new SqlBuilder();
            sb.append(" where `patent_num`=? and deleted=0").arg(jo.getString("patent_num"));
            patent_info = (PatentDTO)patent_dao.get(sb.toString());
        }
        if (patent_info == null){
            exeres = RecordUtils.insertRecord(memory_cache,patent_dao,jo,PatentDTO.class,"patent",expire,null);
        } else {
            List<String> names = new ArrayList<>(),values = new ArrayList<>();
            RecordUtils.compareAndAddList(patent_info,jo,names,values,PatentDTO.class,"patent",null);
            exeres = RecordUtils.updateRecord(memory_cache,patent_dao,patent_info,names,values,PatentDTO.class,"patent",expire,null);
        }
        return exeres;
    }

    // 增加标准信息
    public ExeRes addStandardRecord(JSONObject jo){
        ExeRes exeres = new ExeRes();
        loadDao();
        loadStandardCache();
        // 检查jo中的标准信息、sourceid和categoryid
        ExeRes check_res = RecordUtils.checkKeyFieldsInJSONObject(jo,new String[]{"name","code"});
        if (!check_res.getExe_res()){
            return check_res;
        }
        StandardDTO standard_info = (StandardDTO)memory_cache.get("project"+jo.getString("code"));
        BaseDao standard_dao = getDaoByProject("standard");
        if (standard_info == null){
            SqlBuilder sb = new SqlBuilder();
            sb.append(" where `code`=? and deleted=0").arg(jo.getString("code"));
            standard_info = (StandardDTO)standard_dao.get(sb.toString());
        }
        if (standard_info == null){
            exeres = RecordUtils.insertRecord(memory_cache,standard_dao,jo,StandardDTO.class,"standard",expire,null);
        } else {
            List<String> names = new ArrayList<>(),values = new ArrayList<>();
            RecordUtils.compareAndAddList(standard_info,jo,names,values,StandardDTO.class,"standard",null);
            exeres = RecordUtils.updateRecord(memory_cache,standard_dao,standard_info,names,values,StandardDTO.class,"standard",expire,null);
        }
        return exeres;
    }

    public void json2file(JSONObject jo){
        RecordUtils.writeJSONtoFile(jo);
    }
}
