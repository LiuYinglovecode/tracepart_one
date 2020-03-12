package Dao.YiqingDTO;

import com.alibaba.fastjson.JSONArray;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by huangfei on 2019/5/7.
 */
public interface SqlAnalyticalResults {
    JSONArray analyticalResults(ResultSet rs) throws SQLException;
}
