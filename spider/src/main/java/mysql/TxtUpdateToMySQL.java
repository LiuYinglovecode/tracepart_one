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


    public static boolean dataUpdate(Map<String, String> ipRegionMap) {
        TABLE_NAME = "bde.original_company";
        String sql = "insert into " + TABLE_NAME + "(id,name,company_info,main_product,industry,management_model," +
                "customized_service,register_capital,company_register_time,register_address,company_model,incorporator," +
                "from_where_table_id,processing_method,technics,qhse,product_quality,employees,research_staff," +
                "company_area,sell_area,company_clients,monthly_production,company_turnover,export_fore,company_brand," +
                "quality_control,open_bank,open_account,website,address,contact,fax,postcode,landline,phone,qq,email,business_website,website_name) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, ipRegionMap.get("id"));
            ps.setString(2, ipRegionMap.get("name"));
            ps.setString(3, ipRegionMap.get("company_info"));
            ps.setString(4, ipRegionMap.get("main_product"));
            ps.setString(5, ipRegionMap.get("industry"));
            ps.setString(6, ipRegionMap.get("management_model"));
            ps.setString(7, ipRegionMap.get("customized_service"));
            ps.setString(8, ipRegionMap.get("register_capital"));
            ps.setString(9, ipRegionMap.get("company_register_time"));
            ps.setString(10, ipRegionMap.get("register_address"));
            ps.setString(11, ipRegionMap.get("company_model"));
            ps.setString(12, ipRegionMap.get("incorporator"));
            ps.setString(13, ipRegionMap.get("from_where_table_id"));
            ps.setString(14, ipRegionMap.get("processing_method"));
            ps.setString(15, ipRegionMap.get("technics"));
            ps.setString(16, ipRegionMap.get("qhse"));
            ps.setString(17, ipRegionMap.get("product_quality"));
            ps.setString(18, ipRegionMap.get("employees"));
            ps.setString(19, ipRegionMap.get("research_staff"));
            ps.setString(20, ipRegionMap.get("company_area"));
            ps.setString(21, ipRegionMap.get("sell_area"));
            ps.setString(22, ipRegionMap.get("company_clients"));
            ps.setString(23, ipRegionMap.get("monthly_production"));
            ps.setString(24, ipRegionMap.get("company_turnover"));
            ps.setString(25, ipRegionMap.get("export_fore"));
            ps.setString(26, ipRegionMap.get("company_brand"));
            ps.setString(27, ipRegionMap.get("quality_control"));
            ps.setString(28, ipRegionMap.get("open_bank"));
            ps.setString(29, ipRegionMap.get("open_account"));
            ps.setString(30, ipRegionMap.get("website"));
            ps.setString(31, ipRegionMap.get("address"));
            ps.setString(32, ipRegionMap.get("contact"));
            ps.setString(33, ipRegionMap.get("fax"));
            ps.setString(34, ipRegionMap.get("postcode"));
            ps.setString(35, ipRegionMap.get("landline"));
            ps.setString(36, ipRegionMap.get("phone"));
            ps.setString(37, ipRegionMap.get("qq"));
            ps.setString(38, ipRegionMap.get("email"));
            ps.setString(39, ipRegionMap.get("business_website"));
            ps.setString(40, ipRegionMap.get("website_name"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

}
