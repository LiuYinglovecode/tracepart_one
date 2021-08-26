package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class HerostartToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(HerostartToRedis.class);

    public static void main(String[] args) {
        HerostartToRedis herostartToRedis = new HerostartToRedis();
        herostartToRedis.homepage("http://info.china.herostart.com/");
    }
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "herostart");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.bhead > span > a");
                for (Element e : categoryList) {
                    String href = e.attr("href");
                    ping(href);
                }
            } else {
                LOGGER.info("homepage null");
            }
            LOGGER.info("info.china.herostart.com  DOME");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //

    private void ping(String url) {
        String replace = url.replace(".html", "");
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "herostart");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements doc = document.select("div.pages cite");
                String Total = doc.text().split("/")[1].replace("页", "");
                int total = Integer.valueOf(Total).intValue();//转行类型
                int number = 1;
                for (number = 1; number <= total; number++) {
                    String nextPage = replace + "pn" + number + ".html";
                    category(nextPage);
                }

            } else {
                LOGGER.info("category null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void category(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "herostart");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements detailList = document.select("li.catlist_li a");
                for (Element e : detailList) {
                    String href = e.attr("href");
                    RedisUtil.insertUrlToSet("toCatchUrl", href);
                }
            } else {
                LOGGER.info("category null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
