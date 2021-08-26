package Dao.YiqingDTO;

import Dao.YiqingDTO.MaterialDTO;
import com.yunlu.core.data.sql.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MaterialDao extends BaseDao<MaterialDTO> {
    // 用于继承BaseDao
    @Autowired
    private String dbConfig;
    @Override
    protected Class<MaterialDTO> getBeanType() {
        return MaterialDTO.class;
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
