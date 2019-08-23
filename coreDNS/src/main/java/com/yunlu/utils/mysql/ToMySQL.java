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
        String sql = "insert into " + TABLE_NAME + "(address,dnsin,dnstype,ip)" +
                "values (?,?,?,?)";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, dnsMap.get("address"));
            ps.setString(2, dnsMap.get("dnsin"));
            ps.setString(3, dnsMap.get("dnstype"));
            ps.setString(4, dnsMap.get("ip"));
//            ps.setString(5, dnsMap.get("time"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static boolean bodyToMysql(String dnsBody) {
        TABLE_NAME = "crawler_data.coredns";
        String sql = "insert into " + TABLE_NAME + "(address,dnsin,dnstype,dns,robbmanes,dns7200,dns3600,dns1209600,dns36002)" +
                "values ()";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, dnsBody);
            ps.setString(2, "IN");
            ps.setString(3, "SOA");
            ps.setString(4, "dns.dnstest.moliplayer.com.");
            ps.setString(5, "robbmanes.dnstest.moliplayer.com.");
            ps.setString(6, "7200");
            ps.setString(7, "3600");
            ps.setString(8, "1209600");
            ps.setString(9, "3600");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static List getAllList() {
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

    public static boolean exist(String dnsBody) {
        String sql = "select count(*) as ct from crawler_data.coredns where address=?";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, dnsBody);
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
}
