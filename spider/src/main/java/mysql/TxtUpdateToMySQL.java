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

    public static boolean chinaCnProductsUpdate(Map<String, String> ipRegionMap) {
        TABLE_NAME = "bde.original_products_chinacn";
        String sql = "insert into " + TABLE_NAME + "(product_desc,delivery_place,company_id,delivery_period,total_supply,company_name,prices,product_name) " +
                "values (?,?,?,?,?,?,?,?)";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, ipRegionMap.get("product_desc"));
            ps.setString(2, ipRegionMap.get("delivery_place"));
            ps.setString(3, ipRegionMap.get("company_id"));
            ps.setString(4, ipRegionMap.get("delivery_period"));
            ps.setString(5, ipRegionMap.get("total_supply"));
            ps.setString(6, ipRegionMap.get("company_name"));
            ps.setString(7, ipRegionMap.get("prices"));
            ps.setString(8, ipRegionMap.get("product_name"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static boolean sonhooProductsUpdate(Map<String, String> ipRegionMap) {
        TABLE_NAME = "bde.original_products_sonhoo";
        String sql = "insert into " + TABLE_NAME + "(product_desc,mini_order,company_id,price,company_name,product_specifications,product_name,product_number,release_time) " +
                "values (?,?,?,?,?,?,?,?,?)";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, ipRegionMap.get("product_desc"));
            ps.setString(2, ipRegionMap.get("mini_order"));
            ps.setString(3, ipRegionMap.get("company_id"));
            ps.setString(4, ipRegionMap.get("price"));
            ps.setString(5, ipRegionMap.get("company_name"));
            ps.setString(6, ipRegionMap.get("product_specifications"));
            ps.setString(7, ipRegionMap.get("product_name"));
            ps.setString(8, ipRegionMap.get("product_number"));
            ps.setString(9, ipRegionMap.get("release_time"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static boolean chinaCnCompanyUpdate(Map<String, String> ipRegionMap) {
        TABLE_NAME = "bde.original_company_chinacn";
        String sql = "insert into " + TABLE_NAME + "(business_scope,company_register_time,company_model,address,main_product,register_capital,name,incorporator,id,business_model,company_register_num,home) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?)";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, ipRegionMap.get("business_scope"));
            ps.setString(2, ipRegionMap.get("company_register_time"));
            ps.setString(3, ipRegionMap.get("company_model"));
            ps.setString(4, ipRegionMap.get("address"));
            ps.setString(5, ipRegionMap.get("main_product"));
            ps.setString(6, ipRegionMap.get("register_capital"));
            ps.setString(7, ipRegionMap.get("name"));
            ps.setString(8, ipRegionMap.get("incorporator"));
            ps.setString(9, ipRegionMap.get("id"));
            ps.setString(10, ipRegionMap.get("business_model"));
            ps.setString(11, ipRegionMap.get("company_register_num"));
            ps.setString(12, ipRegionMap.get("home"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static boolean sonhooCompanyUpdate(Map<String, String> ipRegionMap) {
        TABLE_NAME = "bde.original_company_sonhoo";
        String sql = "insert into " + TABLE_NAME + "(address,id,name,landline) " +
                "values (?,?,?,?)";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, ipRegionMap.get("address"));
            ps.setString(2, ipRegionMap.get("id"));
            ps.setString(3, ipRegionMap.get("name"));
            ps.setString(4, ipRegionMap.get("landline"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static boolean sciUpdate(Map<String, String> ipRegionMap) {
        TABLE_NAME = "bde.original_company_sonhoo";
        String sql = "insert into " + TABLE_NAME + "(address,id,name,landline) " +
                "values (?,?,?,?)";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, ipRegionMap.get("address"));
            ps.setString(2, ipRegionMap.get("id"));
            ps.setString(3, ipRegionMap.get("name"));
            ps.setString(4, ipRegionMap.get("landline"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static boolean toocleUpdate(Map<String, String> ipRegionMap) {
        TABLE_NAME = "bde.original_company_toocle";
        String sql = "insert into " + TABLE_NAME + "(id,name,contact,landline,mobile_phone,fax,website,business,address,zip_code) " +
                "values (?,?,?,?,?,?,?,?,?,?)";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, ipRegionMap.get("id"));
            ps.setString(2, ipRegionMap.get("name"));
            ps.setString(3, ipRegionMap.get("contact"));
            ps.setString(4, ipRegionMap.get("landline"));
            ps.setString(5, ipRegionMap.get("mobile_phone"));
            ps.setString(6, ipRegionMap.get("fax"));
            ps.setString(7, ipRegionMap.get("website"));
            ps.setString(8, ipRegionMap.get("business"));
            ps.setString(9, ipRegionMap.get("address"));
            ps.setString(10, ipRegionMap.get("zip_code"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static boolean makepoloUpdate(Map<String, String> ipRegionMap) {
        TABLE_NAME = "bde.original_company_makepolo";
        String sql = "insert into " + TABLE_NAME + "(id,name,contact,website,phone,fax,address,email) " +
                "values (?,?,?,?,?,?,?,?)";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, ipRegionMap.get("id"));
            ps.setString(2, ipRegionMap.get("name"));
            ps.setString(3, ipRegionMap.get("contact"));
            ps.setString(4, ipRegionMap.get("website"));
            ps.setString(5, ipRegionMap.get("phone"));
            ps.setString(6, ipRegionMap.get("fax"));
            ps.setString(7, ipRegionMap.get("address"));
            ps.setString(8, ipRegionMap.get("email"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static boolean huangye88Update(Map<String, String> ipRegionMap) {
        TABLE_NAME = "bde.original_company_huangye88_new";
        String sql = "insert into " + TABLE_NAME + "(id,name,location,type,establishment,main_industry,main_product,contact,phone,detail) " +
                "values (?,?,?,?,?,?,?,?,?,?)";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, ipRegionMap.get("id"));
            ps.setString(2, ipRegionMap.get("name"));
            ps.setString(3, ipRegionMap.get("location"));
            ps.setString(4, ipRegionMap.get("type"));
            ps.setString(5, ipRegionMap.get("establishment"));
            ps.setString(6, ipRegionMap.get("main_industry"));
            ps.setString(7, ipRegionMap.get("main_product"));
            ps.setString(8, ipRegionMap.get("contact"));
            ps.setString(9, ipRegionMap.get("phone"));
            ps.setString(10, ipRegionMap.get("detail"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static boolean shunqiUpdate(Map<String, String> ipRegionMap) {
        TABLE_NAME = "bde.original_company_shunqi";
        String sql = "insert into " + TABLE_NAME + "(id,name,detail,address,landline,contact,mobil_phone,email,fax,main_products,type,city) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?)";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, ipRegionMap.get("id"));
            ps.setString(2, ipRegionMap.get("name"));
            ps.setString(3, ipRegionMap.get("detail"));
            ps.setString(4, ipRegionMap.get("address"));
            ps.setString(5, ipRegionMap.get("landline"));
            ps.setString(6, ipRegionMap.get("contact"));
            ps.setString(7, ipRegionMap.get("mobil_phone"));
            ps.setString(8, ipRegionMap.get("email"));
            ps.setString(9, ipRegionMap.get("fax"));
            ps.setString(10, ipRegionMap.get("main_products"));
            ps.setString(11, ipRegionMap.get("type"));
            ps.setString(12, ipRegionMap.get("city"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static boolean d17Update(Map<String, String> ipRegionMap) {
        TABLE_NAME = "bde.original_company_d17";
        String sql = "insert into " + TABLE_NAME + "(id,name,contact,mobil_phone,QQ,email,landline,fax,address,zip_code,url,detail) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?)";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, ipRegionMap.get("id"));
            ps.setString(2, ipRegionMap.get("name"));
            ps.setString(3, ipRegionMap.get("contact"));
            ps.setString(4, ipRegionMap.get("mobil_phone"));
            ps.setString(5, ipRegionMap.get("QQ"));
            ps.setString(6, ipRegionMap.get("email"));
            ps.setString(7, ipRegionMap.get("landline"));
            ps.setString(8, ipRegionMap.get("fax"));
            ps.setString(9, ipRegionMap.get("address"));
            ps.setString(10, ipRegionMap.get("zip_code"));
            ps.setString(11, ipRegionMap.get("url"));
            ps.setString(12, ipRegionMap.get("detail"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }


    public static boolean ali1688Update(Map<String, String> ipRegionMap) {
        TABLE_NAME = "bde.original_company";
        String sql = "insert into " + TABLE_NAME + "(id,name,company_info,main_product,industry,management_model," +
                "customized_service,register_capital,company_register_time,register_address,company_model,incorporator," +
                "from_where_table_id,processing_method,technics,quality_control,product_quality,employees,research_staff," +
                "company_area,sell_area,company_clients,monthly_production,company_turnover,export_fore,company_brand," +
                "quality_controls,open_bank,open_account,website,address,contacts,fax,postcode,landline,telephone) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
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
            ps.setString(16, ipRegionMap.get("quality_control"));
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
            ps.setString(27, ipRegionMap.get("quality_controls"));
            ps.setString(28, ipRegionMap.get("open_bank"));
            ps.setString(29, ipRegionMap.get("open_account"));
            ps.setString(30, ipRegionMap.get("website"));
            ps.setString(31, ipRegionMap.get("address"));
            ps.setString(32, ipRegionMap.get("contacts"));
            ps.setString(33, ipRegionMap.get("fax"));
            ps.setString(34, ipRegionMap.get("postcode"));
            ps.setString(35, ipRegionMap.get("landline"));
            ps.setString(36, ipRegionMap.get("telephone"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
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

    public static boolean ichangjiaUpdate(Map<String, String> ipRegionMap) {
        TABLE_NAME = "bde.original_company";
        String sql = "insert into " + TABLE_NAME + "(id,name,company_info,contact,phone,address,website,landline) " +
                "values (?,?,?,?,?,?,?)";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, ipRegionMap.get("id"));
            ps.setString(2, ipRegionMap.get("name"));
            ps.setString(3, ipRegionMap.get("company_info"));
            ps.setString(4, ipRegionMap.get("contact"));
            ps.setString(5, ipRegionMap.get("phone"));
            ps.setString(6, ipRegionMap.get("address"));
            ps.setString(7, ipRegionMap.get("website"));
            ps.setString(8, ipRegionMap.get("landline"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static boolean chemmUpdate(Map<String, String> ipRegionMap) {
        TABLE_NAME = "bde.original_company";
        String sql = "insert into " + TABLE_NAME + "(id,name,contact,landline,phone,fax,address,postcode,website,email,main_product,website_name,company_info,management_model) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, ipRegionMap.get("id"));
            ps.setString(2, ipRegionMap.get("name"));
            ps.setString(3, ipRegionMap.get("contact"));
            ps.setString(4, ipRegionMap.get("landline"));
            ps.setString(5, ipRegionMap.get("phone"));
            ps.setString(6, ipRegionMap.get("fax"));
            ps.setString(7, ipRegionMap.get("address"));
            ps.setString(8, ipRegionMap.get("postcode"));
            ps.setString(9, ipRegionMap.get("website"));
            ps.setString(10, ipRegionMap.get("email"));
            ps.setString(11, ipRegionMap.get("main_product"));
            ps.setString(12, ipRegionMap.get("website_name"));
            ps.setString(13, ipRegionMap.get("company_info"));
            ps.setString(14, ipRegionMap.get("management_model"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

}
