package Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * P:所有标点字符
 * Z：所有分隔符字符
 * S:所有符号
 * M:所有音调符号标记
 * C:所有控制字符
 */
public class NewsMd5 {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewsMd5.class);

    public static String newsMd5(String text) {
        try {
            if (null != text) {
                String str = text.replaceAll("[\\pP\\pZ\\pS\\pM\\pC]", "");
                if (str.length() > 400) {
                    return MD5Util.getMD5String(str.substring(0, 200) + str.substring(text.length() - 200, text.length()));
                } else {
                    return MD5Util.getMD5String(str);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        String str = "!!！？？!!!!%*          ）%￥！KT123456V去符号标号！！当然,，。!!..**半角";
        NewsMd5.newsMd5(str);
    }
}
