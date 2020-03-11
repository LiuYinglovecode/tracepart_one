package Dao.YiqingDTO;

import Dao.YiqingDTO.ProductDTO;
import com.yunlu.core.data.sql.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductDao extends BaseDao<ProductDTO> {
    // 用于继承BaseDao
    @Autowired
    private String dbConfig;
    @Override
    protected Class<ProductDTO> getBeanType() {
        return ProductDTO.class;
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
