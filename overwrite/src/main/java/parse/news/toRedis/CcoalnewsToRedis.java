package parse.news.toRedis;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.RedisUtil;
import util.HttpUtil;

/**
 * @author liyujie
 */
public class CcoalnewsToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(CcoalnewsToRedis.class);

    public void getUrlStart(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国煤炭网");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categories = document.select(".nav li a");
                for (Element e : categories) {
                    String category = e.text().trim();
                    if ("要闻".equals(category) || "独家".equals(category) || "人事".equals(category) || "经济".equals(category) ||
                            "企业新闻".equals(category) || "煤炭人".equals(category) || "价格指数".equals(category)) {
                        String categoryUrl = e.attr("href");
                        newsList(categoryUrl);
                    }
                }
            }
            LOGGER.info("www.ccoalnews.com DONE");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void newsList(String categoryUrl) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(categoryUrl, "中国煤炭网");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements seedUrlList = document.select(".listPage-l.fl  ul:not(.pages) li a");
                for (Element e : seedUrlList) {
                    RedisUtil.insertUrlToSet("toCatchUrl", e.attr("href"));
                }
                Elements nestPage = document.select(".page-next");
                if (!"0".equals(String.valueOf(nestPage.size()))) {
                    newsList(nestPage.select("a").attr("href"));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
