package seedUrl.news.ccoalnews;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CcoalnewsMainEntry {
    private static final Logger LOGGER = LoggerFactory.getLogger(CcoalnewsMainEntry.class);
    private static SimpleDateFormat time = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss", Locale.CHINA);
    private static String url = "http://www.ccoalnews.com/index.html";

    public static void main(String[] args) {
        try {
            CcoalnewsParseToRedis ccoalnewsParseToRedis = new CcoalnewsParseToRedis();
            ccoalnewsParseToRedis.getUrlStart(url);
            LOGGER.info("Ccoalnews DONE : " + time.format(new Date()));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
