package spider.patent.zhiqueip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class zhiqueipEntry {
    private final static Logger LOGGER = LoggerFactory.getLogger(zhiqueipEntry.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.CHINA);
    private static String patentList = "https://www.zhiqueip.com/result?keyword=&type=1&page=1&exp=&tradeStatus=&dbType=&ipc=&prices=&caseStatus=&legalStatus=&sort=0";

    public static void main(String[] args) {
        zhiqueipParse zhiqueipParse = new zhiqueipParse();
        zhiqueipParse.patentList(patentList);
        LOGGER.info("zhiqueip DONE : " + timestamp.format(new Date()));
    }
}
