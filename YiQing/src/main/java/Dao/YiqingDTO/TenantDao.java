package Dao.YiqingDTO;

import Dao.YiqingDTO.TenantDTO;
import com.yunlu.core.data.sql.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TenantDao extends BaseDao<TenantDTO> {
    // 用于继承BaseDao
    @Autowired
    private String dbConfig;
    @Override
    protected Class<TenantDTO> getBeanType() {
        return TenantDTO.class;
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
