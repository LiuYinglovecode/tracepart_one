package mysql;

import com.zaxxer.hikari.HikariConfig;

import com.zaxxer.hikari.HikariDataSource;
import config.ConfigClient;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by liuqiang on 2016/10/14.
 */
public class DbConnectionPool {
    private static HashMap<String, DbConnectionPool> _poolInstances = new HashMap<>();

    /**
     * 默认名称为maindb
     *
     * @return
     */
    public static DbConnectionPool getPool() {
        return getPool(null);
    }

    /**
     * 获取指定名称的数据库连接池
     *
     * @param poolName
     * @return
     */
    public static DbConnectionPool getPool(String poolName) {
        if (poolName == null || poolName.length() == 0) {
            poolName = "maindb";
        }

        if (_poolInstances.containsKey(poolName)) {
            return _poolInstances.get(poolName);
        }

        DbConnectionPool result = null;
        synchronized (_poolInstances) {
            if (!_poolInstances.containsKey(poolName)) {
                result = new DbConnectionPool(poolName);
                _poolInstances.put(poolName, result);
            } else {
                result = _poolInstances.get(poolName);
            }
        }
        return result;
    }

    //连接池
    private HikariDataSource dataSource;
    private String _poolName;

    public DbConnectionPool() {
        this(null);
    }

    public DbConnectionPool(String poolName) {
        if (poolName == null || poolName.length() == 0) {
            poolName = "maindb";
        }

        _poolName = poolName;

//        连接池配置信息
//        String dbUrl = ConfigClient.instance().get(poolName, "url");
//        String dbUser = ConfigClient.instance().get(poolName, "user");
//        String dbPassword = ConfigClient.instance().get(poolName, "sec.password");
//        String maxPoolSize = ConfigClient.instance().get(poolName, "maxpoolsize");
//        String idleTimeout = ConfigClient.instance().get(poolName, "idletimeout");
//        String connectionTimeout = ConfigClient.instance().get(poolName, "connectionTimeout");
//        String driverClassName = ConfigClient.instance().get(poolName, "driverClassName");

        String dbUrl = "jdbc:mysql://106.74.146.210:4244/crawler_data?characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
        String dbUser = "crawler_rw";
        String dbPassword = "p8@a^@AE0IrNmONM";
        String maxPoolSize = "10";
        String idleTimeout = "1200000";
        String connectionTimeout = "60000";
        String driverClassName = "com.mysql.jdbc.Driver";

//        String dbUrl = "jdbc:mysql://127.0.0.1:3306/crawler_data?characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
//        String dbUser = "root";
//        String dbPassword = "root";


        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        if (dbUser != null && dbUser.length() > 0) {
            config.setUsername(dbUser);
        }

        if (dbPassword != null && dbPassword.length() > 0) {
            config.setPassword(dbPassword);
        }

        if (driverClassName != null && driverClassName.length() > 0) {
            config.setDriverClassName(driverClassName);
        }

        if (maxPoolSize != null && maxPoolSize.length() > 0) {
            config.setMaximumPoolSize(Integer.parseInt(maxPoolSize));
        }

        //空闲时间
        if (idleTimeout != null && idleTimeout.length() > 0) {
            config.setIdleTimeout(Integer.parseInt(idleTimeout));
        }

        //连接超时时间
        if (connectionTimeout != null && connectionTimeout.length() > 0) {
            config.setConnectionTimeout(Integer.parseInt(connectionTimeout));
        }

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        //链接池初始化
        setDataSource(new HikariDataSource(config));
    }

    //获取链接
    public Connection getConnection() {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    //关闭连接池
    public void close() {
        dataSource.close();

        synchronized (_poolInstances) {
            if (_poolInstances.containsKey(_poolName)) {
                _poolInstances.remove(_poolName);
            }
        }
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    private void setDataSource(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }
}
