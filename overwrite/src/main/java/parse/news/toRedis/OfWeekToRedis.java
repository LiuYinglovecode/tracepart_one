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
    private static String links =  "https://www.ofweek.com/";

    public static void main(String[] args) {
        OfWeekToRedis ofWeekToRedis = new OfWeekToRedis();
        ofWeekToRedis.homepage("https://www.ofweek.com/CATList-8100-CHANGYIEXINWE.html");
    }

    /**
     * 新闻首页：解析页面，拿到新闻分类的url
     *
     * @param url
     */
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Element elements = document.select("div.list-num > a").last().previousElementSibling();
                String text = elements.text();
                String attr = elements.attr("href");
                for (int i = 1; i <= Integer.parseInt(text); i++) {
                    String replace =links + attr.replace(text, String.valueOf(i));
                    newsList(replace);
                }
            } else {
                LOGGER.info("Page parsing failed...");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements elements = document.select("span.wen > h3 > a");
                if (!elements.isEmpty()) {
                    for (Element element : elements) {
                        String attr =element.attr("href");
                        RedisUtil.insertUrlToSet("toCatchUrl", attr);
                    }
                } else {
                    LOGGER.info("Beyond the total page boundaries...");
                }
            } else {
                LOGGER.info("Page parsing failed...");

            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
