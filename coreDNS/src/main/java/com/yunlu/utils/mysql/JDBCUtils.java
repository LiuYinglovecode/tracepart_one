package com.yunlu.utils.mysql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;

/**
 * Created by liuqiang on 2016/10/14.
 */
public class JDBCUtils {
    private final static Logger _logger = LoggerFactory.getLogger(JDBCUtils.class);

    /**
     * 获取ResultSet列名及列的index，列名小写
     *
     * @param resultSet
     * @return
     * @throws SQLException
     */
    public static HashMap<String, Integer> getFieldMapping(ResultSet resultSet) throws SQLException {
        HashMap<String, Integer> mapping = new HashMap<>();
        if (resultSet == null) {
            return mapping;
        }

        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            String columnName = metaData.getColumnName(i + 1).toLowerCase();
            if (!mapping.containsKey(columnName)) {
                mapping.put(columnName, i + 1);
            }
        }

        return mapping;
    }

    /**
     * 获取ResultSet列名(别名)及列的index，列名小写
     *
     * @param resultSet
     * @return
     * @throws SQLException
     */
    public static HashMap<String, Integer> getFieldAliasMapping(ResultSet resultSet) throws SQLException {
        HashMap<String, Integer> mapping = new HashMap<>();
        if (resultSet == null) {
            return mapping;
        }

        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            String columnName = metaData.getColumnLabel(i + 1).toLowerCase();
            if (!mapping.containsKey(columnName)) {
                mapping.put(columnName, i + 1);
            }
        }

        return mapping;
    }


    /**
     * 关闭连接
     */
    public static void close(Connection conn, PreparedStatement ps, ResultSet rs) {
        close(null, ps, rs, conn);
    }

    /**
     * 关闭连接
     */
    public static void close(Connection conn, Statement statement, ResultSet rs) {
        close(statement, null, rs, conn);
    }

    /**
     * 关闭连接
     */
    public static void close(PreparedStatement ps, ResultSet rs, Connection conn) {
        close(null, ps, rs, conn);
    }

    /**
     * 关闭连接
     */
    public static void close(Statement statement, PreparedStatement ps, ResultSet rs, Connection conn) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {

            }
            statement = null;
        }

        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {

            }
            rs = null;
        }

        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {

            }
            ps = null;
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                _logger.error(e.getMessage());
            }
            conn = null;
        }
    }

}
