package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

public class WjwToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(WjwToRedis.class);
    private static String baseUrl = "http://news.wjw.cn";


    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "wjw");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.information > div > ul > li > a");
                for (Element e : categoryList) {
                    String href = e.attr("href");
                    if (href.contains("/NewsList")) {
                        ping(baseUrl + href);
                    }
                }
            } else {
                LOGGER.info("homepage null");
            }
            LOGGER.info("news.wjw.cn  DONE");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void ping(String url) {
        String replace = url.replace(".xhtml", "");
        try {
            int total = 1000;
            int number = 1;
            for (number = 1; number <= total; number++) {
                String nextPage = replace + "-" + number + ".xhtml";
                category(nextPage);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void category(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "wjw");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements detailList = document.select("div.informationListL_02 > ul > li > p > a");
                for (Element e : detailList) {
                    String link = baseUrl + e.attr("href");
                    RedisUtil.insertUrlToSet("toCatchUrl", link);
                }
            } else {
                LOGGER.info("category null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
