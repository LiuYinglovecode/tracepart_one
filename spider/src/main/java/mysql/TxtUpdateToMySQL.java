package mysql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class TxtUpdateToMySQL {
    private final static Logger LOGGER = LoggerFactory.getLogger(TxtUpdateToMySQL.class);
    private static final String CONF_SECTION = "bde_testdb";
    private static String TABLE_NAME = "";

    protected static Connection getConnection() {
        DbConnectionPool pool = DbConnectionPool.getPool(CONF_SECTION);
        Connection conn = pool.getConnection();
        return conn;
    }


    public static boolean taojindiUpdate(Map<String, String> ipRegionMap) {
        TABLE_NAME = "bde.original_company";
        String sql = "insert into " + TABLE_NAME + "(id,name,contact,industry,company_info,address,postcode,qq,landline,fax,phone,website,website_name) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, ipRegionMap.get("id"));
            ps.setString(2, ipRegionMap.get("name"));
            ps.setString(3, ipRegionMap.get("contact"));
            ps.setString(4, ipRegionMap.get("industry"));
            ps.setString(5, ipRegionMap.get("company_info"));
            ps.setString(6, ipRegionMap.get("address"));
            ps.setString(7, ipRegionMap.get("postcode"));
            ps.setString(8, ipRegionMap.get("qq"));
            ps.setString(9, ipRegionMap.get("landline"));
            ps.setString(10, ipRegionMap.get("fax"));
            ps.setString(11, ipRegionMap.get("phone"));
            ps.setString(12, ipRegionMap.get("website"));
            ps.setString(13, ipRegionMap.get("website_name"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

}
