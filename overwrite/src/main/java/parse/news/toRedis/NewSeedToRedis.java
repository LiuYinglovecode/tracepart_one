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

public class NewSeedToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewSeedToRedis.class);

    public void homepage(String url) {
        try {
            String s = null;
            int i = 1607;
            for (int j = 1; j <= i; j++) {
                s = url + "p" + j;

                String html = HttpUtil.httpGetwithJudgeWord(s, "关于我们");
                Thread.sleep(SleepUtils.sleepMin());
                Document doc = Jsoup.parse(html);
                Elements link = doc.select("#news-list > ul > li > h3 > a");
                for (Element element : link) {
                    String links = element.attr("href");
                    RedisUtil.insertUrlToSet("toCatchUrl", links);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
