package Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewsMd5 {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewsMd5.class);

    public static String newsMd5(String text) {
        try {
            if (null != text) {
                if (text.length() > 50) {
                    return MD5Util.getMD5String(text.substring(0, 25) + text.substring(text.length() - 25, text.length()));
                } else {
                    return MD5Util.getMD5String(text);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        String str = "";
        NewsMd5.newsMd5(str);
    }
}
