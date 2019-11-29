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

public class ZNDSToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZNDSToRedis.class);
    private static String links =  "https://news.znds.com";

    public static void main(String[] args) {
        ZNDSToRedis zNDSToRedis = new ZNDSToRedis();
        zNDSToRedis.homepage("https://news.znds.com/");
    }

    /**
     * 新闻首页：解析页面，拿到新闻分类的url
     * @param url
     */
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "znds");
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements elements = document.select("ul.nav > li > strong > a");
                for (Element element : elements){
                    if (!element.text().contains("首页") && !element.text().contains("视频")){
                        String attr = links.concat(element.attr("href"));
                        newsList(attr);
                        paging(attr);
                    }
                }
            } else {
                LOGGER.info("Page parsing failed...");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void paging(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "znds");
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Element elements = document.select("div.page > a").last();
                String text = elements.attr("href").split("_")[2].replace(".html","").trim();
                String attr = elements.attr("href");
                for (int i = 2; i <= Integer.parseInt(text); i++) {
                    String replace = url.concat(attr.replace(text, String.valueOf(i)));
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
            String html = HttpUtil.httpGetwithJudgeWord(url, "znds");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements elements = document.select("div.listl.list2 > ul > li > h3 > a");
                if (!elements.isEmpty()) {
                    for (Element element : elements) {
                        String attr =links.concat(element.attr("href"));
                        RedisUtil.insertUrlToSet("toCatchUrl", attr);
                    }
                }
            } else {
                LOGGER.info("Page parsing failed...");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
