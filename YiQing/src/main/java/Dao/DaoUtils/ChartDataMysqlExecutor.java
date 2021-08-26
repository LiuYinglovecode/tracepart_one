package Dao.DaoUtils;

import Dao.YiqingDTO.SqlAnalyticalResults;
import com.alibaba.fastjson.JSONArray;
import com.yunlu.core.data.redis.RedisClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by huangfei on 2019/8/19.
 */
@Component
public class ChartDataMysqlExecutor implements ChartDataExecutor {
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private DataSource dataSource;
    /**
     * kylin查询sql,并在回掉中分别处理结果集，只有一条记录，也统一用array返回
     * @param sqlStr
     * @param rsCallBack
     * @return
     */
    @Override
    public JSONArray executeSql(String sqlStr, SqlAnalyticalResults rsCallBack) {

        //先看redis中是否有数据
        //String sqlMd5 = CommonUtil.sqlConvertToMD5(sqlStr);
        //String redisResultString = redisClient.get(CommonUtil.redisKey(sqlMd5));
        //if (StringUtils.isNotEmpty(redisResultString)) {
        //    return JSON.parseArray(redisResultString);
        //}

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            JDBCUtils.close(ps, rs, conn);
            return new JSONArray();
        }

        JSONArray resAarray = new JSONArray();
        //boolean hasException = false;
        try {
            ps = conn.prepareStatement(sqlStr);
            rs = ps.executeQuery();
            //取出查询结果，放的JSONArray里，供service层使用
            resAarray = rsCallBack.analyticalResults(rs);
        } catch (SQLException e) {
            //hasException = true;
            e.printStackTrace();
        } finally {
            JDBCUtils.close(ps, rs, conn);
        }

        //数据库中数据写入redis,并设置超时
        //if (resAarray != null && !hasException) {
        //    redisClient.setexpire(CommonUtil.redisKey(sqlMd5), resAarray.toString(), CommonUtil.REDIS_EXPIRE_SECOND);
        //}
        return resAarray;
    }

    public int executeInsertSql(String sqlStr, SqlAnalyticalResults rsCallBack) {

        //先看redis中是否有数据
        String sqlMd5 = CommonUtil.sqlConvertToMD5(sqlStr);
        String redisResultString = redisClient.get(CommonUtil.redisKey(sqlMd5));
        if (StringUtils.isNotEmpty(redisResultString)) {
            //return JSON.parseArray(redisResultString);
            return 0;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int affected = 0;
        try {
            conn = dataSource.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            JDBCUtils.close(ps, rs, conn);
            //return new JSONArray();
            return 0;
        }

        JSONArray resAarray = new JSONArray();
        boolean hasException = false;
        try {
            ps = conn.prepareStatement(sqlStr);
//            rs = ps.executeUpdate();
//            //取出查询结果，放的JSONArray里，供service层使用
//            resAarray = rsCallBack.analyticalResults(rs);
            affected =  ps.executeUpdate();
        } catch (SQLException e) {
            hasException = true;
            e.printStackTrace();
        } finally {
            JDBCUtils.close(ps, rs, conn);
        }

        //数据库中数据写入redis,并设置超时
        if (resAarray != null && !hasException) {
            redisClient.setex(CommonUtil.redisKey(sqlMd5), CommonUtil.REDIS_EXPIRE_SECOND, resAarray.toString());
        }
//        return resAarray;
        return affected;
    }

    public int executeUpdateSql(String sqlStr, SqlAnalyticalResults rsCallBack) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int affected = 0;
        try {
            conn = dataSource.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            JDBCUtils.close(ps, rs, conn);
            return 0;
        }

        try {
            ps = conn.prepareStatement(sqlStr);
            affected =  ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(ps, rs, conn);
        }
        return affected;
    }
}
