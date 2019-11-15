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

public class RfidworldToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(RfidworldToRedis.class);
    private static String link = new String("http://news.rfidworld.com.cn");

    public static void main(String[] args) {
        RfidworldToRedis rfidworldToRedis = new RfidworldToRedis();
        rfidworldToRedis.homepage("http://news.rfidworld.com.cn/");
    }

    //首页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "rfidworld");
//            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("#divNavColumn > p > a");
                if (!categoryList.isEmpty()) {
                    for (Element e : categoryList) {
                        if (e.attr("href").contains("list")) {
                            String href = link.concat(e.attr("href"));
//                        System.out.println(href);
                            newsList(href);
                            paing(href);
                        }
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

            String html = HttpUtil.httpGetwithJudgeWord(href, "rfidworld");
            Document document = Jsoup.parse(html);
            String tailPage = document.select("#ctl00_C_AspNetPager > div > a").last().attr("href");
            String pageCount = document.select("#ctl00_C_AspNetPager > div").eq(0).text().split("共")[0].split("/")[1].trim();
            int i;
            for (i = 1; i <= Integer.parseInt(pageCount); i++) {
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
            String html = HttpUtil.httpGetwithJudgeWord(url, "rfidworld");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("div.nListL > ul > li > a");
                if (!newsListInfo.isEmpty()) {
                    for (Element e : newsListInfo) {
                        if (e.attr("href").contains("http")) {
                            RedisUtil.insertUrlToSet("toCatchUrl", e.attr("href"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
