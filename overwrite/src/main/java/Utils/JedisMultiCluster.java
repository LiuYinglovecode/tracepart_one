package seedUrl.Utils;

import config.ConfigClient;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class JedisMultiCluster {
    private static HashMap<String, JedisCluster> _jedisCluster = new HashMap<>();

    private static JedisCluster getCluster() {
        return getCluster(null);
    }

    /**
     * 根据连接池名称获取Redis连接池
     */
    private static JedisCluster getCluster(final String poolName) {
        JedisCluster cluster = null;
        String newPoolName = poolName;
        if (poolName == null || poolName.length() == 0) {
            newPoolName = "redis";
        }

        if (!_jedisCluster.containsKey(newPoolName)) {
            synchronized (_jedisCluster) {
                if (!_jedisCluster.containsKey(newPoolName)) {
                    cluster = createCluster(newPoolName);
                    _jedisCluster.put(newPoolName, cluster);
                }
            }
        } else {
            cluster = _jedisCluster.get(newPoolName);
        }

        return cluster;
    }

    private static JedisCluster createCluster(final String poolName) {
        JedisCluster jedisCluster = null;
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            //最大连接数, 默认8个
            config.setMaxTotal(8);

            //获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
            config.setMaxWaitMillis(-1);

            //逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
            config.setMinEvictableIdleTimeMillis(1800000);

            //最小空闲连接数, 默认0
            config.setMinIdle(0);

            //每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
            config.setNumTestsPerEvictionRun(3);

            //对象空闲多久后逐出, 当空闲时间>该值 且 空闲连接>最大空闲数 时直接逐出,不再根据MinEvictableIdleTimeMillis判断  (默认逐出策略)
            config.setSoftMinEvictableIdleTimeMillis(1800000);

            //在获取连接的时候检查有效性, 默认false
            config.setTestOnBorrow(false);

            //在空闲时检查有效性, 默认false
            config.setTestWhileIdle(false);

            //逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
            config.setTimeBetweenEvictionRunsMillis(-1);

            // 添加集群的服务节点Set集合
            Set<HostAndPort> hostAndPortsSet = new HashSet<HostAndPort>();
            // 添加节点

            String connectionTimeout = ConfigClient.instance().get(poolName, "connectionTimeout", "5000");
            String soTimeout = ConfigClient.instance().get(poolName, "soTimeout", "5000");
            String maxAttempts = ConfigClient.instance().get(poolName, "maxAttempts", "6");
            String passwd = ConfigClient.instance().get(poolName, "passwd", null);

            jedisCluster = new redis.clients.jedis.JedisCluster(hostAndPortsSet, Integer.parseInt(connectionTimeout), Integer.parseInt(soTimeout), Integer.parseInt(maxAttempts), passwd, config);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jedisCluster;
    }

    /**
     * 从默认连接池获取jedis实例
     *
     * @return
     */
    public static JedisCluster getJedis() {
        return getJedis(null);
    }

    /**
     * 从指定连接池名称中获取jedis实例
     *
     * @param poolName
     * @return
     */
    public synchronized static JedisCluster getJedis(final String poolName) {
        try {
            redis.clients.jedis.JedisCluster cluster = getCluster(poolName);
            if (cluster != null) {
                return cluster;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}