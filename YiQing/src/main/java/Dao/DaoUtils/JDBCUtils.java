package Dao.DaoUtils;

import com.yunlu.core.config.ConfigClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * Created by huangfei on 2019/5/7.
 */
public class JDBCUtils {
    private final static Logger _logger = LoggerFactory.getLogger(JDBCUtils.class);

    public static final String DB_DRIVERCLASSNAME = ConfigClient.instance().get("kylindb","driverclassname","org.apache.kylin.jdbc.Driver");
    public static final String DB_URL = ConfigClient.instance().get("kylindb","url","jdbc:kylin://kylin.inter.htres.cn/cockpit");
    public static final String DB_USERNAME = ConfigClient.instance().get("kylindb","user","ADMIN");
    public static final String DB_PASSWORD = ConfigClient.instance().get("kylindb","sec.password","KYLIN");
    //KYLIN中查询监控数值的CM库地址
    public static final String DB_CM_URL = ConfigClient.instance().get("kylindb","cm_url","jdbc:kylin://kylin.inter.htres.cn/cockpit");

    /**关闭连接*/
    public static void close(Connection conn, PreparedStatement ps){
        close(null, ps, null, conn);
    }

    /**关闭连接*/
    public static void close(Connection conn, PreparedStatement ps, ResultSet rs){
        close(null, ps, rs, conn);
    }

    /**关闭连接*/
    public static void close(Connection conn, Statement statement, ResultSet rs){
        close(statement, null, rs, conn);
    }

    /**关闭连接*/
    public static void close(PreparedStatement ps, ResultSet rs, Connection conn){
        close(null, ps, rs, conn);
    }

    /**关闭连接*/
    public static void close(Statement statement, PreparedStatement ps, ResultSet rs, Connection conn){
        if( statement != null){
            try {
                statement.close();
            } catch (SQLException e) {

            }
            statement = null;
        }

        if( rs != null){
            try {
                rs.close();
            } catch (SQLException e) {

            }
            rs = null;
        }

        if( ps != null){
            try {
                ps.close();
            } catch (SQLException e) {

            }
            ps = null;
        }

        if( conn != null){
            try{
                conn.close();
            }catch(SQLException e){
                _logger.error(e.getMessage());
            }
            conn = null;
        }
    }
}
