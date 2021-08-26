package Dao.YiqingDTO;

import Dao.YiqingDTO.MaterialCategoryDTO;
import com.yunlu.core.data.sql.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MaterialCategoryDao extends BaseDao<MaterialCategoryDTO> {
    // 用于继承BaseDao
    @Autowired
    private String dbConfig;
    @Override
    protected Class<MaterialCategoryDTO> getBeanType() {
        return MaterialCategoryDTO.class;
    }
    @Override
    protected String getConfig() {
        return dbConfig;
    }
    @Override
    public String getPrimaryKey() {
        return "id";
    }
}
