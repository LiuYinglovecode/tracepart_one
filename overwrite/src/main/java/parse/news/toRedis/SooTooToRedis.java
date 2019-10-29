package parse.news.toRedis;

import Utils.RedisUtil;
import Utils.SleepUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class SooTooToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(SooTooToRedis.class);
    private static String baseUrl = "http://www.iyiou.com";

    //首页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "sootoo");
//            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("ul.nav.navbar-nav > li > a");
                if (!categoryList.isEmpty()) {
                    for (Element e : categoryList) {
                        String href = e.attr("href");
//                        System.out.println(href);
                        paing(href);
                    }
                }
            }else {
                LOGGER.info("null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void paing(String href) {
        try {

            String html = HttpUtil.httpGetwithJudgeWord(href, "sootoo");
            Document document = Jsoup.parse(html);
            String firstPage = document.select("a.current").attr("href");
            String pageCount = document.select("div.pagination.clearfix > span").text().split("/ ")[1];
            int i;
            for (i = 1; i <=Integer.parseInt(pageCount); i++) {
                String links = firstPage+"/page/"+String.valueOf(i);
                newsList(links);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    //新闻列表
    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "sootoo");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("h2.item-title > a");
                if (!newsListInfo.isEmpty()) {
                    for (Element e : newsListInfo) {
                        RedisUtil.insertUrlToSet("toCatchUrl", e.attr("href"));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
