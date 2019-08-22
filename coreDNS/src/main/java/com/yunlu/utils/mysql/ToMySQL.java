package com.yunlu.utils.mysql;

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

public class ToMySQL {
    private final static Logger LOGGER = LoggerFactory.getLogger(ToMySQL.class);
    private static final String CONF_SECTION = "bde_testdb";
    private static String TABLE_NAME = "";

    protected static Connection getConnection() {
        DbConnectionPool pool = DbConnectionPool.getPool(CONF_SECTION);
        Connection conn = pool.getConnection();
        return conn;
    }

    public static boolean coreDNSToMysql(Map<String, String> dnsMap) {
        TABLE_NAME = "crawler_data.coredns";
        String sql = "insert into " + TABLE_NAME + "(address,dnsin,dnstype,ip,time)" +
                "values (?,?,?,?,?)";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, dnsMap.get("address"));
            ps.setString(2, dnsMap.get("dnsin"));
            ps.setString(3, dnsMap.get("dnstype"));
            ps.setString(4, dnsMap.get("ip"));
            ps.setString(5, dnsMap.get("time"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static List getAllList() {
//        TABLE_NAME = tableName;
        String sql = "select * from crawler_data.coredns";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            List list = new ArrayList();
            while (rs.next()) {
                list.add(rs.getString("address"));
            }
            return list;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return null;
    }
}
