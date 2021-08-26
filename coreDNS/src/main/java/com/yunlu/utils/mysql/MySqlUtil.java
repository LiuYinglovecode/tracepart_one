package com.yunlu.utils.mysql;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunlu.core.config.ConfigClient;
import com.yunlu.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class MySqlUtil {
    private final static Logger LOGGER = LoggerFactory.getLogger(MySqlUtil.class);
    private static final String CONF_SECTION = "bde_testdb";
    private static String TABLE_NAME = "";

    protected static Connection getConnection() {
        DbConnectionPool pool = DbConnectionPool.getPool(CONF_SECTION);
        Connection conn = pool.getConnection();
        return conn;
    }

    public static boolean addDns(Map<String, String> dnsMap, String tableName) {
        String sql = "insert into " + tableName + "(address,dnsin,dnstype,ip,time)" +
                "values (?,?,?,?,?)";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, dnsMap.get("address"));
            ps.setString(2, dnsMap.get("dnsin"));
            ps.setString(3, dnsMap.get("dnstype"));
            ps.setString(4, dnsMap.get("ip"));
            ps.setString(5, TimeUtil.getTime());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static boolean addBaseDns(String baseDns, String tableName) {
        String sql = "insert into " + tableName + "(address,dnsin,dnstype,dns,robbmanes,dns7200,dns3600,dns1209600,dns36002,time)" +
                "values (?,?,?,?,?,?,?,?,?,?)";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, baseDns);
            ps.setString(2, "IN");
            ps.setString(3, "SOA");
            ps.setString(4, "dns.dnstest.htres.cn.");
            ps.setString(5, "robbmanes.dnstest.htres.cn.");
            ps.setString(6, "7200");
            ps.setString(7, "3600");
            ps.setString(8, "1209600");
            ps.setString(9, "3600");
            ps.setString(10, TimeUtil.getTime());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static JSONArray getAllList(String tableName) {
        String sql = "select * from " + tableName;
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            JSONArray list = new JSONArray();
            while (rs.next()) {
                JSONObject object = new JSONObject();
                object.put("address", rs.getString("address"));
                object.put("dnsin", rs.getString("dnsin"));
                object.put("dnstype", rs.getString("dnstype"));
                object.put("ip", rs.getString("ip"));
                object.put("time", rs.getString("time"));
                object.put("dns", rs.getString("dns"));
                object.put("robbmanes", rs.getString("robbmanes"));
                object.put("dns7200", rs.getString("dns7200"));
                object.put("dns3600", rs.getString("dns3600"));
                object.put("dns1209600", rs.getString("dns1209600"));
                object.put("dns36002", rs.getString("dns36002"));
                list.add(object);
            }
            return list;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return null;
    }

    public static boolean exist(String baseDns, String tableName) {
        String sql = "select count(*) as ct from " + tableName + " where address=?";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, baseDns);
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

    public static boolean deleteDns(String address, String tableName) {
        String sql = "delete from " + tableName + " where address=?";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, address);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static boolean updateDns(Map<String, String> dnsMap, String address, String tableName) {
        String sql = "UPDATE " + tableName + " SET address=?,dnsin=?,dnstype=?,ip=?,time=? WHERE address='" + address + "'";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, dnsMap.get("address"));
            ps.setString(2, dnsMap.get("dnsin"));
            ps.setString(3, dnsMap.get("dnstype"));
            ps.setString(4, dnsMap.get("ip"));
            ps.setString(5, TimeUtil.getTime());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return false;
    }

    public static JSONObject getDns(String address, String tableName) {
        String sql = "select * from " + tableName + " where address='" + address + "'";
        Connection connection = getConnection();
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            JSONObject object = new JSONObject();
            while (rs.next()) {
                object.put("address", rs.getString("address"));
                object.put("dnsin", rs.getString("dnsin"));
                object.put("dnstype", rs.getString("dnstype"));
                object.put("ip", rs.getString("ip"));
            }
            return object;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            JDBCUtils.close(ps, null, connection);
        }
        return null;
    }

}
