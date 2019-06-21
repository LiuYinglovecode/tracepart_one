package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

public class Ca800ToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(Ca800ToRedis.class);

    public void homepage(String url) {
        try {
            String replace = url.replace("1.html", "");
            int number = 1;
            int total = 11341;
            for (number = 1; number < total; number++) {
                String nextPage = replace + number + ".html";
                String html = HttpUtil.httpGetwithJudgeWord(nextPage, "中国自动化网");
                if (null != html) {
                    Document document = Jsoup.parse(html);
                    Elements categoryList = document.select("div.newslist_title a");
                    for (Element element : categoryList) {
                        String href = "http://www.ca800.com" + element.attr("href");
                        RedisUtil.insertUrlToSet("toCatchUrl", href);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
