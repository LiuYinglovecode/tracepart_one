package ipregion;

import mysql.DbConnectionPool;
import mysql.JDBCUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class IpRegionTaobaoDao {
    private final static Logger LOGGER = LoggerFactory.getLogger(IpRegionTaobaoDao.class);
    private static final String CONF_SECTION = "bde_testdb";
    private static final String TABLE_NAME = "bde.ip_region_taobao";
    protected static Connection getConnection() {
        DbConnectionPool pool = DbConnectionPool.getPool(CONF_SECTION);
        Connection conn = pool.getConnection();
        return conn;
    }
    public static boolean insertIpRegion(Map<String,String> ipRegionMap){
        String sql = "insert into " + TABLE_NAME + "(ip,country,area,region,city,county,isp,country_id,area_id,region_id,city_id,county_id,isp_id) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, ipRegionMap.get("ip"));
            ps.setString(2, ipRegionMap.get("country"));
            ps.setString(3, ipRegionMap.get("area"));
            ps.setString(4, ipRegionMap.get("region"));
            ps.setString(5, ipRegionMap.get("city"));
            ps.setString(6, ipRegionMap.get("county"));
            ps.setString(7, ipRegionMap.get("isp"));
            ps.setString(8, ipRegionMap.get("country_id"));
            ps.setString(9, ipRegionMap.get("area_id"));
            ps.setString(10, ipRegionMap.get("region_id"));
            ps.setString(11, ipRegionMap.get("city_id"));
            ps.setString(12, ipRegionMap.get("county_id"));
            ps.setString(13, ipRegionMap.get("isp_id"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }finally {
            JDBCUtils.close(ps,null, connection);
        }
        return false;
    }
}
