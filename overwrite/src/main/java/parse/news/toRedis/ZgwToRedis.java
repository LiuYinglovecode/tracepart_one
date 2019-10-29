package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class ZgwToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZgwToRedis.class);
    private static String baseUrl = "http://news.zgw.com";

    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "zgw");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("li > font > a");
                for (Element e : categoryList) {
                    String href = baseUrl+e.attr("href");
                    System.out.println(href);
                    ping(href);
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
            String html = HttpUtil.httpGetwithJudgeWord(url, "zgw");
            Document document = Jsoup.parse(html);
            String Total = document.select("div.fenye a").last().previousElementSibling().text();
            int total = Integer.valueOf(Total).intValue();
            int number = 1;
            for (number = 1; number <= total; number++) {
                String nextPage = url + "page_" + number + ".html";
//                System.out.println("下一页：" + nextPage);
                category(nextPage);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void category(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "zgw");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements detailList = document.select("div.trends h3 a");
                if (detailList.size()!=0) {
                    for (Element e : detailList) {
                        RedisUtil.insertUrlToSet("toCatchUrl", e.attr("href"));
                    }

                }else {
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
