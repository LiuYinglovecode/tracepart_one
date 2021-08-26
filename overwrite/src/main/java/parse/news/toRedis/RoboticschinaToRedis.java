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

public class RoboticschinaToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoboticschinaToRedis.class);
    private static String link = new String("https://www.roboticschina.com");

    public static void main(String[] args) {
        RoboticschinaToRedis roboticschinaToRedis = new RoboticschinaToRedis();
        roboticschinaToRedis.homepage("https://www.roboticschina.com/news/");
    }

    //首页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "roboticschina");
//            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("li.col-sm-1> a");
                if (!categoryList.isEmpty()) {
                    for (Element e : categoryList) {
                        String href = e.attr("href");
//                        System.out.println(href);
                        newsList(href);
                        paing(href);
                    }
                }
            } else {
                LOGGER.info("null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void paing(String href) {
        try {

            String html = HttpUtil.httpGetwithJudgeWord(href, "roboticschina");
            Document document = Jsoup.parse(html);
            String tailPage = document.select("#splitpage > ul > li.lastly > a").attr("href");
            String pageCount = document.select("#splitpage > span").text().split("/共 ")[1];
            int i;
            for (i = 2; i <= Integer.parseInt(pageCount); i++) {
                String replace = tailPage.replace(pageCount, String.valueOf(i));
                String links = link.concat(replace);
                newsList(links);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    //新闻列表
    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "roboticschina");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("div.thetitle > a");
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
