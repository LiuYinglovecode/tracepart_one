package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class OfWeekToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(OfWeekToRedis.class);

    /**
     * 新闻首页：解析页面，拿到新闻分类的url
     *
     * @param url
     */
    public void homepage(String url) {
        try {
            int number;
            for (number = 1; number <= 41670; number++) {
                String links = url.replace(".html","").concat("-").concat(String.valueOf(number)).concat(".html");
                String html = HttpUtil.httpGetwithJudgeWord(links, "关于我们");
                if (!html.isEmpty()) {
                    Document document = Jsoup.parse(html);
                    Elements elements = document.select("span.wen > h3 > a");
                    if (!elements.isEmpty()) {
                        for (Element element : elements) {
                            RedisUtil.insertUrlToSet("toCatchUrl",element.attr("href"));
                        }
                    }else {
                        LOGGER.info("Beyond the total page boundaries...");
                    }
                } else {
                    LOGGER.info("Page parsing failed...");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
