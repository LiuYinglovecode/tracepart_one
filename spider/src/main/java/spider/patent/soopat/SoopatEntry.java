package spider.patent.soopat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SoopatEntry {
    private final static Logger LOGGER = LoggerFactory.getLogger(SoopatEntry.class);
    private static SimpleDateFormat crawlerDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        try {
            SoopatParse soopat = new SoopatParse();
            soopat.crawlerStart();
            LOGGER.info("SoopatParse DONE :" + crawlerDate.format(new Date()));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
