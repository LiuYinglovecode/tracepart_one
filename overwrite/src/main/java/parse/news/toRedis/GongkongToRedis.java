package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;


public class GongkongToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(GongkongToRedis.class);
    private static String baseUrl = "http://www.gongkong.com";

    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "联系我们");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.nav_news_d > ul > li > a");
                for (Element e : categoryList) {
                    if (e.text().contains("业界动态")||e.text().contains("新品速递")) {
                        String href =e.attr("href");
//                        System.out.println(href);
//                        Thread.sleep(2000);
                        ping(href);
                    }
                }
            } else {
                LOGGER.info("homepage null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void ping(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "联系我们");
            Document doc = Jsoup.parse(html);
            String select = doc.select("span.gk_page_label")
                    .text().split("/")[1].replace("页","");
            if (null!=select) {
                int count = Integer.parseInt(select);
                int number;
                for (number = 1; number <= count; number++) {
                    String s = String.valueOf(number);
                    String link = url.replace("_1", "_" + s);
//                    System.out.println(replace);
//                    Thread.sleep(2000);
                    category(link);
                }
            }else {
                category(url);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void category(String link) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(link, "联系我们");
//            Thread.sleep(2000);
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements detailList = document.select("table.news_list01 > tbody > tr > td > a");
                if (0 != detailList.size()) {
                    for (Element e : detailList) {
                        RedisUtil.insertUrlToSet("toCatchUrl",(baseUrl + e.attr("href")));
                    }
                }else {
                    LOGGER.info("该页面为空");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
