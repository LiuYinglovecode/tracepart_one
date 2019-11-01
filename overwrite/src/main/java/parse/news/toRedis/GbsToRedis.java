package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class GbsToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(GbsToRedis.class);
    private static String baseUrl = "http://www.gbs.cn";

    public static void main(String[] args) {
        GbsToRedis gbsToRedis = new GbsToRedis();
        gbsToRedis.homepage("http://www.gbs.cn/info/");
    }
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "gbs");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.infl_bq > div > ul > li > a");
                for (Element e : categoryList) {
                    String href = baseUrl + e.attr("href");
                    ping(href);
                }
            } else {
                LOGGER.info("homepage null");
            }
            LOGGER.info("www.gbs.cn/info  DONE");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void ping(String url) {
        String replace = url.replace(".html", "");
        try {
            int total = 215;
            int number = 1;
            for (number = 1; number <= total; number++) {
                String nextPage = replace + "-p" + number + ".html";
                category(nextPage);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void category(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "gbs");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements detailList = document.select("div.lis_txt > ul > li > a");
                if (detailList.size() != 0) {
                    for (Element e : detailList) {
                        RedisUtil.insertUrlToSet("toCatchUrl", baseUrl + e.attr("href"));
                    }

                } else {
                    LOGGER.info("该页面为空");
                }
            } else {
                LOGGER.info("category null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
