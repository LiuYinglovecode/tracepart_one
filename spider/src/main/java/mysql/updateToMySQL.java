package mysql;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class updateToMySQL {
    private final static Logger LOGGER = LoggerFactory.getLogger(updateToMySQL.class);
    private static final String CONF_SECTION = "bde_testdb";
    private static String TABLE_NAME = "";

    protected static Connection getConnection() {
        DbConnectionPool pool = DbConnectionPool.getPool(CONF_SECTION);
        Connection conn = pool.getConnection();
        return conn;
    }


    public static boolean companyInsert(Map<String, String> ipRegionMap) {
        TABLE_NAME = "crawler_data.crawler_company";
        String sql = "insert into " + TABLE_NAME + "(id,name,company_info,main_product,industry,management_model," +
                "customized_service,register_capital,company_register_time,register_address,company_model,incorporator," +
                "from_where_table_id,processing_method,technics,qhse,product_quality,employees,research_staff," +
                "company_area,sell_area,company_clients,monthly_production,company_turnover,export_fore,company_brand," +
                "quality_control,open_bank,open_account,website,address,contact,fax,postcode,landline,phone,qq,email," +
                "business_website,crawlerId,createTime) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
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
            ps.setString(40, ipRegionMap.get("crawlerId"));
            ps.setString(41, ipRegionMap.get("createTime"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static boolean exist(Map<String, String> ipRegionMap, String tableName, String MD5) {
        TABLE_NAME = tableName;
        String sql = "select count(*) as ct from " + TABLE_NAME + " where id=?";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, MD5);
            ResultSet Judge = ps.executeQuery();
            Judge.next();
            int ct = Judge.getInt("ct");
            if (ct == 0) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static boolean exist2(Map<String, String> ipRegionMap, String tableName, String newsId, String type) {
//        TABLE_NAME = tableName;
        String sql = "select count(*) as ct from " + tableName + " where " + type + "=?";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, newsId);
            ResultSet Judge = ps.executeQuery();
            Judge.next();
            int ct = Judge.getInt("ct");
            if (ct == 0) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static List getCompanyNameList(String tableName) {
        TABLE_NAME = tableName;
        String sql = "select name from " + TABLE_NAME;
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
//            ps.setString(1, MD5);
            ResultSet rs = ps.executeQuery();
            List list = new ArrayList();
            while (rs.next()) {
                list.add(rs.getString(1));
            }
            return list;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return null;
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

    public static boolean sonhooCompanyInsert(Map<String, String> ipRegionMap) {
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

    public static boolean sonhooCompanyUpdate(Map<String, String> ipRegionMap, String MD5) {
        TABLE_NAME = "bde.original_company_sonhoo";
        String sql = "UPDATE " + TABLE_NAME + " SET address=?,name=?,landline=? WHERE id='" + MD5 + "'";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, ipRegionMap.get("address"));
//            ps.setString(2, ipRegionMap.get("id"));
            ps.setString(2, ipRegionMap.get("name"));
            ps.setString(3, ipRegionMap.get("landline"));
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

    public static boolean toocleInsert(Map<String, String> ipRegionMap) {
        TABLE_NAME = "bde.original_company_toocle";
        String sql = "insert into " + TABLE_NAME + "(id,name,contact,landline,phone,fax,website,business_website,address,postcode) " +
                "values (?,?,?,?,?,?,?,?,?,?)";
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
            ps.setString(7, ipRegionMap.get("website"));
            ps.setString(8, ipRegionMap.get("business_website"));
            ps.setString(9, ipRegionMap.get("address"));
            ps.setString(10, ipRegionMap.get("postcode"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static boolean toocleUpdate(Map<String, String> ipRegionMap, String MD5) {
        TABLE_NAME = "bde.original_company_toocle";
        String sql = "UPDATE " + TABLE_NAME + " SET name=?,contact=?,landline=?,phone=?,fax=?,website=?,business_website=?,address=?,postcode=? WHERE id='" + MD5 + "'";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
//            ps.setString(1, ipRegionMap.get("id"));
            ps.setString(1, ipRegionMap.get("name"));
            ps.setString(2, ipRegionMap.get("contact"));
            ps.setString(3, ipRegionMap.get("landline"));
            ps.setString(4, ipRegionMap.get("phone"));
            ps.setString(5, ipRegionMap.get("fax"));
            ps.setString(6, ipRegionMap.get("website"));
            ps.setString(7, ipRegionMap.get("business_website"));
            ps.setString(8, ipRegionMap.get("address"));
            ps.setString(9, ipRegionMap.get("postcode"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static boolean makepoloInsert(Map<String, String> ipRegionMap) {
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

    public static boolean makepoloUpdate(Map<String, String> ipRegionMap, String MD5) {
        TABLE_NAME = "bde.original_company_makepolo";
        String sql = "UPDATE " + TABLE_NAME + " SET name=?,contact=?,website=?,phone=?,fax=?,address=?,email=? WHERE id='" + MD5 + "'";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
//            ps.setString(1, ipRegionMap.get("id"));
            ps.setString(1, ipRegionMap.get("name"));
            ps.setString(2, ipRegionMap.get("contact"));
            ps.setString(3, ipRegionMap.get("website"));
            ps.setString(4, ipRegionMap.get("phone"));
            ps.setString(5, ipRegionMap.get("fax"));
            ps.setString(6, ipRegionMap.get("address"));
            ps.setString(7, ipRegionMap.get("email"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static boolean huangye88Insert(Map<String, String> ipRegionMap) {
        TABLE_NAME = "bde.original_company_huangye88_new";
        String sql = "insert into " + TABLE_NAME + "(id,name,address,company_model,company_register_time,industry,main_product,contact,phone,company_info) " +
                "values (?,?,?,?,?,?,?,?,?,?)";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, ipRegionMap.get("id"));
            ps.setString(2, ipRegionMap.get("name"));
            ps.setString(3, ipRegionMap.get("address"));
            ps.setString(4, ipRegionMap.get("company_model"));
            ps.setString(5, ipRegionMap.get("company_register_time"));
            ps.setString(6, ipRegionMap.get("industry"));
            ps.setString(7, ipRegionMap.get("main_product"));
            ps.setString(8, ipRegionMap.get("contact"));
            ps.setString(9, ipRegionMap.get("phone"));
            ps.setString(10, ipRegionMap.get("company_info"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static boolean huangye88Update(Map<String, String> ipRegionMap, String MD5) {
        TABLE_NAME = "bde.original_company_huangye88_new";
        String sql = "UPDATE " + TABLE_NAME + " SET name=?,address=?,company_model=?,company_register_time=?,industry=?,main_product=?,contact=?,phone=?,company_info=? where id='" + MD5 + "'";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
//            ps.setString(1, ipRegionMap.get("id"));
            ps.setString(1, ipRegionMap.get("name"));
            ps.setString(2, ipRegionMap.get("address"));
            ps.setString(3, ipRegionMap.get("company_model"));
            ps.setString(4, ipRegionMap.get("company_register_time"));
            ps.setString(5, ipRegionMap.get("industry"));
            ps.setString(6, ipRegionMap.get("main_product"));
            ps.setString(7, ipRegionMap.get("contact"));
            ps.setString(8, ipRegionMap.get("phone"));
            ps.setString(9, ipRegionMap.get("company_info"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static boolean shunqiInsert(Map<String, String> ipRegionMap) {
        TABLE_NAME = "bde.original_company_shunqi";
        String sql = "insert into " + TABLE_NAME + "(id,name,company_info,address,landline,contact,phone,email,fax,main_product,company_model,register_address) " +
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

    public static boolean shunqiUpdate(Map<String, String> ipRegionMap, String MD5) {
        TABLE_NAME = "bde.original_company_shunqi";
        String sql = "UPDATE " + TABLE_NAME + " SET name=?,company_info=?,address=?,landline=?,contact=?,phone=?,email=?,fax=?,main_product=?,company_model=?,register_address=? WHERE id='" + MD5 + "'";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
//            ps.setString(1, ipRegionMap.get("id"));
            ps.setString(1, ipRegionMap.get("name"));
            ps.setString(2, ipRegionMap.get("detail"));
            ps.setString(3, ipRegionMap.get("address"));
            ps.setString(4, ipRegionMap.get("landline"));
            ps.setString(5, ipRegionMap.get("contact"));
            ps.setString(6, ipRegionMap.get("mobil_phone"));
            ps.setString(7, ipRegionMap.get("email"));
            ps.setString(8, ipRegionMap.get("fax"));
            ps.setString(9, ipRegionMap.get("main_products"));
            ps.setString(10, ipRegionMap.get("type"));
            ps.setString(11, ipRegionMap.get("city"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static boolean d17Insert(Map<String, String> ipRegionMap) {
        TABLE_NAME = "bde.original_company_d17";
        String sql = "insert into " + TABLE_NAME + "(id,name,contact,phone,qq,email,landline,fax,address,postcode,website,company_info) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?)";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, ipRegionMap.get("id"));
            ps.setString(2, ipRegionMap.get("name"));
            ps.setString(3, ipRegionMap.get("contact"));
            ps.setString(4, ipRegionMap.get("phone"));
            ps.setString(5, ipRegionMap.get("qq"));
            ps.setString(6, ipRegionMap.get("email"));
            ps.setString(7, ipRegionMap.get("landline"));
            ps.setString(8, ipRegionMap.get("fax"));
            ps.setString(9, ipRegionMap.get("address"));
            ps.setString(10, ipRegionMap.get("postcode"));
            ps.setString(11, ipRegionMap.get("website"));
            ps.setString(12, ipRegionMap.get("company_info"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static boolean d17Update(Map<String, String> ipRegionMap, String MD5) {
        TABLE_NAME = "bde.original_company_d17";
        String sql = "UPDATE " + TABLE_NAME + " SET name=?,contact=?,phone=?,qq=?,email=?,landline=?,fax=?,address=?,postcode=?,website=?,company_info=? WHERE id='" + MD5 + "'";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
//            ps.setString(1, ipRegionMap.get("id"));
            ps.setString(1, ipRegionMap.get("name"));
            ps.setString(2, ipRegionMap.get("contact"));
            ps.setString(3, ipRegionMap.get("phone"));
            ps.setString(4, ipRegionMap.get("qq"));
            ps.setString(5, ipRegionMap.get("email"));
            ps.setString(6, ipRegionMap.get("landline"));
            ps.setString(7, ipRegionMap.get("fax"));
            ps.setString(8, ipRegionMap.get("address"));
            ps.setString(9, ipRegionMap.get("postcode"));
            ps.setString(10, ipRegionMap.get("website"));
            ps.setString(11, ipRegionMap.get("company_info"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }


    public static boolean standardInsert(Map<String, String> ipRegionMap) {
        TABLE_NAME = "bde.original_standard";
        String sql = "insert into " + TABLE_NAME + "(name,category,industry,codeName,downloadUrl,crawlerId,createTime) " +
                "values (?,?,?,?,?,?,?)";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, ipRegionMap.get("name"));
            ps.setString(2, ipRegionMap.get("category"));
            ps.setString(3, ipRegionMap.get("industry"));
            ps.setString(4, ipRegionMap.get("codeName"));
            ps.setString(5, ipRegionMap.get("downloadUrl"));
            ps.setString(6, ipRegionMap.get("crawlerId"));
            ps.setString(7, ipRegionMap.get("createTime"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static boolean patentInsert(Map<String, String> ipRegionMap) {
        TABLE_NAME = "crawler_data.crawler_patent";
        String sql = "insert into " + TABLE_NAME + "(patentName, applicationNumber, applicationDate, applicant, inventor, currentPatentee, publicNumber, publicDate, mainClassificationNumber, classificationNumber, nationalCode, address, abstract,crawlerId,state) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, ipRegionMap.get("patentName"));
            ps.setString(2, ipRegionMap.get("applicationNumber"));
            ps.setString(3, ipRegionMap.get("applicationDate"));
            ps.setString(4, ipRegionMap.get("applicant"));
            ps.setString(5, ipRegionMap.get("inventor"));
            ps.setString(6, ipRegionMap.get("currentPatentee"));
            ps.setString(7, ipRegionMap.get("publicNumber"));
            ps.setString(8, ipRegionMap.get("publicDate"));
            ps.setString(9, ipRegionMap.get("mainClassificationNumber"));
            ps.setString(10, ipRegionMap.get("classificationNumber"));
            ps.setString(11, ipRegionMap.get("nationalCode"));
            ps.setString(12, ipRegionMap.get("address"));
            ps.setString(13, ipRegionMap.get("abstract"));
            ps.setString(14, ipRegionMap.get("crawlerId"));
            ps.setString(15, ipRegionMap.get("state"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static boolean datasourceUpdate(Map<String, String> ipRegionMap) {
        TABLE_NAME = "crawler_data.crawler_source";
        String sql = "insert into " + TABLE_NAME + "(id,website,url,type,createTime) " +
                "values (?,?,?,?,?)";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, ipRegionMap.get("id"));
            ps.setString(2, ipRegionMap.get("website"));
            ps.setString(3, ipRegionMap.get("url"));
            ps.setString(4, ipRegionMap.get("type"));
            ps.setString(5, ipRegionMap.get("createTime"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }


    public static boolean newsUpdate(Map<String, String> ipRegionMap, String name, String type) {
        TABLE_NAME = "crawler_data.crawler_news";
        String sql = "UPDATE " + TABLE_NAME + " SET title=?,time=?,author=?,text=?,amountOfReading=?,source=?,plate=?,crawlerId=?,url=?,images=?,newsId=? WHERE " + type + "='" + name + "'";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
//            ps.setString(1, ipRegionMap.get("id"));
            ps.setString(1, ipRegionMap.get("title"));
            ps.setString(2, ipRegionMap.get("time"));
            ps.setString(3, ipRegionMap.get("author"));
            ps.setString(4, ipRegionMap.get("text"));
            ps.setString(5, ipRegionMap.get("amountOfReading"));
            ps.setString(6, ipRegionMap.get("source"));
            ps.setString(7, ipRegionMap.get("plate"));
            ps.setString(8, ipRegionMap.get("crawlerId"));
            ps.setString(9, ipRegionMap.get("url"));
            ps.setString(10, ipRegionMap.get("images"));
            ps.setString(11, ipRegionMap.get("newsId"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static boolean newsInsert(Map<String, String> ipRegionMap) {
        TABLE_NAME = "crawler_data.crawler_news";
        String sql = "insert into " + TABLE_NAME + "(title, time, author, text, amountOfReading, source, plate, crawlerId,url,images,newsId) " +
                "values (?,?,?,?,?,?,?,?,?,?,?)";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, ipRegionMap.get("title"));
            ps.setString(2, ipRegionMap.get("time"));
            ps.setString(3, ipRegionMap.get("author"));
            ps.setString(4, ipRegionMap.get("text"));
            ps.setString(5, ipRegionMap.get("amountOfReading"));
            ps.setString(6, ipRegionMap.get("source"));
            ps.setString(7, ipRegionMap.get("plate"));
            ps.setString(8, ipRegionMap.get("crawlerId"));
            ps.setString(9, ipRegionMap.get("url"));
            ps.setString(10, ipRegionMap.get("images"));
            ps.setString(11, ipRegionMap.get("newsId"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static boolean productUpdate(Map<String, String> ipRegionMap) {
        TABLE_NAME = "crawler_data.crawler_product";
        String sql = "insert into " + TABLE_NAME + "(id,product_name,company_id,company_name,product_desc,prices,delivery_place,delivery_period" +
                ",total_supply,mini_order,product_specifications,product_number,release_time,trade_category,product_introduce,detailUrl,tradeParameter" +
                ",production_place,contactInformation,contacts,product_feature,product_applications,product_brand,crawlerId,product_images) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, ipRegionMap.get("id"));
            ps.setString(2, ipRegionMap.get("product_name"));
            ps.setString(3, ipRegionMap.get("company_id"));
            ps.setString(4, ipRegionMap.get("company_name"));
            ps.setString(5, ipRegionMap.get("product_desc"));
            ps.setString(6, ipRegionMap.get("prices"));
            ps.setString(7, ipRegionMap.get("delivery_place"));
            ps.setString(8, ipRegionMap.get("delivery_period"));
            ps.setString(9, ipRegionMap.get("total_supply"));
            ps.setString(10, ipRegionMap.get("mini_order"));
            ps.setString(11, ipRegionMap.get("product_specifications"));
            ps.setString(12, ipRegionMap.get("product_number"));
            ps.setString(13, ipRegionMap.get("release_time"));
            ps.setString(14, ipRegionMap.get("trade_category"));
            ps.setString(15, ipRegionMap.get("product_introduce"));
            ps.setString(16, ipRegionMap.get("detailUrl"));
            ps.setString(17, ipRegionMap.get("tradeParameter"));
            ps.setString(18, ipRegionMap.get("production_place"));
            ps.setString(19, ipRegionMap.get("contactInformation"));
            ps.setString(20, ipRegionMap.get("contacts"));
            ps.setString(21, ipRegionMap.get("product_feature"));
            ps.setString(22, ipRegionMap.get("product_applications"));
            ps.setString(23, ipRegionMap.get("product_brand"));
            ps.setString(24, ipRegionMap.get("crawlerId"));
            ps.setString(25, ipRegionMap.get("product_images"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }
}
