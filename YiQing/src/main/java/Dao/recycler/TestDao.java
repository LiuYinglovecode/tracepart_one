package Dao.recycler;

import Dao.YiqingDTO.TestDTO;
import com.yunlu.core.data.sql.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestDao extends BaseDao<TestDTO> {
    // 用于继承BaseDao
    @Autowired
    private String dbConfig;
    @Override
    protected Class<TestDTO> getBeanType() {
        return TestDTO.class;
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
