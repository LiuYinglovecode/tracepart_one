package parse.news.toRedis;

import Utils.HttpUtil;
import Utils.RedisUtil;
import Utils.SleepUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NkjToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(NkjToRedis.class);
    private static String links =  "https://www.nkj.cn/zixun";

    public static void main(String[] args) {
        NkjToRedis nkjToRedis = new NkjToRedis();
        nkjToRedis.homepage("https://www.nkj.cn/zixun");
    }

    /**
     * 新闻首页：解析页面，拿到新闻分类的url
     * @param url
     */
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "牛科技");
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements elements = document.select("a.next.page-numbers").prev();
                String text = elements.text().replace(",","").trim();
                String attr = elements.attr("href");
                for (int i = 1; i <= Integer.parseInt(text); i++) {
                    String replace = attr.replace(text, String.valueOf(i));
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
            String html = HttpUtil.httpGetwithJudgeWord(url, "牛科技");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements elements = document.select("div.posts-gallery-content > h2 > a");
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
