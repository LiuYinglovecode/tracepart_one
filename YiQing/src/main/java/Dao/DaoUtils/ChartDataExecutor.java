package Dao.DaoUtils;

import Dao.YiqingDTO.SqlAnalyticalResults;
import com.alibaba.fastjson.JSONArray;

/**
 * Created by huangfei on 2019/6/25.
 * 查询执行器，kylin查询执行器，mysql查询执行器等分别实现该接口
 */
public interface ChartDataExecutor {
    JSONArray executeSql(String sqlStr, SqlAnalyticalResults rsCallBack);
}
