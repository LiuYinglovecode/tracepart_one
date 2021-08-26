package Dao.DaoUtils;

import com.yunlu.core.config.ConfigClient;
import org.springframework.util.DigestUtils;


/**
 * Created by huangfei on 2019/5/7.
 * Edited By Hastings on 2020/3/7
 */
public class CommonUtil {

    //redis超时时间
    public static final int REDIS_EXPIRE_SECOND = Integer.parseInt(ConfigClient.instance().get("redis","expire.second","3600"));

    public static String redisKey(String sqlMd5) {
        return ("cockpitapp-" + sqlMd5);
    }

    public static String sqlConvertToMD5(String sqlStr) {
        String base = sqlStr;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }


}
