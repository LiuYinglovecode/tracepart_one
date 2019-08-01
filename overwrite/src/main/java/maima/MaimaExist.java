package maima;

import Utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tracepart.util.Writer;
import util.HttpUtil;

import java.util.HashMap;
import java.util.Map;

public class MaimaExist {
    private static final Logger LOGGER = LoggerFactory.getLogger(Maima.class);
    private static String maimaToCatche = "maimaToCatche";
    private static String outPath = "F:/Maima.csv";
    private static Map<String, String> header = null;

    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    public static void main(String[] args) {
        try {
            MaimaExist maimaExist = new MaimaExist();
            maimaExist.getUrl();
            LOGGER.info("DONE");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void getUrl() {
        try {
            String maimaUrl = "";
            while (null != (maimaUrl = RedisUtil.getUrlFromeSet(maimaToCatche))) {
                try {
//                    maimaUrl = "http://eoss.casicloud.com/home/cockpit/personal.html";
                    String html = HttpUtil.httpGet(maimaUrl, header);
                    if (!"".equals(html) && html.contains("<script type=\"text/javascript\" src=\"https://stat.htres.cn")) {
                        String maima = html.substring(html.indexOf("<script type=\"text/javascript\" src=\"https://stat.htres.cn") + 36, html.indexOf("\"", html.indexOf("<script type=\"text/javascript\" src=\"https://stat.htres.cn") + 36));
                        String id = maima.split("\\?", 2)[1];
                        Writer.writer("true" + "," + maimaUrl + "," + maima + "," + id, outPath);
                    } else if (!"".equals(html) && html.contains("<script type=\"text/javascript\" src=\"http://stat.htres.cn")) {
                        String maima = html.substring(html.indexOf("<script type=\"text/javascript\" src=\"http://stat.htres.cn") + 36, html.indexOf("\"", html.indexOf("<script type=\"text/javascript\" src=\"https://stat.htres.cn") + 36));
                        String id = maima.split("\\?", 2)[1];
                        Writer.writer("true" + "," + maimaUrl + "," + maima + "," + id, outPath);
                    } else if (!"".equals(html) && !html.contains("<script type=\"text/javascript\" src=\"https://stat.htres.cn") && !html.contains("<script type=\"text/javascript\" src=\"http://stat.htres.cn")) {
                        Writer.writer("false" + "," + maimaUrl, outPath);
                    } else if ("".equals(html)) {
                        Writer.writer("bad url" + "," + maimaUrl, outPath);
                    }
                } catch (Exception e) {
                    Writer.writer("err url" + "," + maimaUrl, outPath);
                    LOGGER.error(e.getMessage());
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
