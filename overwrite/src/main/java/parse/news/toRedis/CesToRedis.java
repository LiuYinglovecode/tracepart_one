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

public class CesToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(CesToRedis.class);

    //首页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国节能网");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.Nav ul li a");
                for (Element element : categoryList) {
                    if (!element.text().contains("新闻首页")) {
                        String href = element.attr("href");
                        String plate = element.text().trim();
                        paging(href, plate);
                    }
                }
            }
            LOGGER.info("www.ces.cn  DONE");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页
    private void paging(String url, String plate) {
        String replace = url.replace(".html", "");
        try {
            ArrayList<String> list = new ArrayList<>();
            int number = 1;
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国节能网");
            Document document = Jsoup.parse(html);
            String Total = document.select("#page-nav.pagination cite").text().split("/")[1].replace("页", "");
            int total = Integer.valueOf(Total).intValue();
            for (number = 1; number < total + 1; number++) {
                String nextPage = replace + "-" + number + ".html";
                list.add(nextPage);
            }
            for (String link : list) {
                newsList(link, plate);

            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //新闻列表
    private void newsList(String url, String plate) {

        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国节能网");
            Document document = Jsoup.parse(html);
            Elements newsListInfo = document.select("div.fl.name a");
            for (Element e : newsListInfo) {
                String href = e.attr("href");
                RedisUtil.insertUrlToSet("toCatchUrl", href);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

    }
}
