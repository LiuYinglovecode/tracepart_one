package Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCluster;

public class RedisUtil {
    private final static Logger LOGGER = LoggerFactory.getLogger(RedisUtil.class);

    /**
     * @param key
     * @param member
     * @return 放入set集合
     */
    public static boolean insertUrlToSet(String key, String member) {
        JedisCluster jedis = null;
        try {
            jedis = JedisMultiCluster.getJedis();
            if (null != jedis) {
                jedis.sadd(key, member);
                return true;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }

    /**
     * @param key
     * @return 移除并返回集合中的一个随机元素。
     */
    public static String getUrlFromeSet(String key) {
        JedisCluster jedis = null;
        try {
            jedis = JedisMultiCluster.getJedis();
            String Str = jedis.spop(key);
            if (null != Str) {
                return Str;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    /**
     * @param key
     * @param member
     * @return
     */
    public static boolean isExist(String key, String member) {
        JedisCluster jedis = null;
        try {
            jedis = JedisMultiCluster.getJedis();
            return jedis.sismember(key, member);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }


    public static int getCountFromKey(String key) {
        JedisCluster jedis = null;
        try {
            jedis = JedisMultiCluster.getJedis();
            jedis.smembers(key);
            return jedis.scard(key).intValue();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return -1;
    }
}
