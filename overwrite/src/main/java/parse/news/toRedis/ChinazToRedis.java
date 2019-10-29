package parse.news.toRedis;

import Utils.RedisUtil;
import Utils.SleepUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;


public class ChinazToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChinazToRedis.class);


    public void paging(String href) {
        try {
            String s = null;
            int i = 9036;
            for (int j = 1; j <= i; j++) {
                if (1 == j) {
                    s = href + "index.shtml";
                } else {
                    s = href + j + ".shtml";
                }
                String html = HttpUtil.httpGetwithJudgeWord(s, "联系我们");
                Thread.sleep(SleepUtils.sleepMin());
                Document document = Jsoup.parse(html);
                Elements link = document.select("div.catlist-box > h4 > a");
                for (Element element : link) {
                    String links = "http:" + element.attr("href");
                    RedisUtil.insertUrlToSet("toCatchUrl",links);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
