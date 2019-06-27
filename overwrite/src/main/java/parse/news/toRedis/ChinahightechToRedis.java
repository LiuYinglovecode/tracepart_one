package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

import java.util.ArrayList;

public class ChinahightechToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChinahightechToRedis.class);
    private static final String homepage = "http://www.chinahightech.com";

    //主页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国高新网");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("#nav li ul li a");
                for (Element element : categoryList) {
                    String href = homepage + element.attr("href");
                    String plate = element.text();
                    newsList(href, plate);
                    paging(href, plate);
                }
            }
            LOGGER.info("www.chinahightech.com  DONE");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页
    private void paging(String url, String plate) {
        try {
            ArrayList<String> list = new ArrayList<>();
            int number = 2;
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国高新网");
            if (html != null) {
                Document document = Jsoup.parse(html);
                String Total = document.select("#pages.text-c a.a1").last().previousElementSibling().text();
                int total = Integer.valueOf(Total).intValue();
                for (number = 2; number < total + 1; number++) {
                    String nextPage = url + number + ".html";
                    list.add(nextPage);
                }
                for (String link : list) {
                    newsList(link, plate);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //新闻列表
    private void newsList(String url, String plate) {
        ArrayList<String> list = new ArrayList<>();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国高新网");
            if (html != null) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("#index-gz.list-a ul li h3 a");
                for (Element e : newsListInfo) {
                    String href = e.attr("href");
                    list.add(href);
                }
            }
            for (String link : list) {
                RedisUtil.insertUrlToSet("toCatchUrl", link);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
