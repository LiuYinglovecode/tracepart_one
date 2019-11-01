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

public class IyiouToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(IyiouToRedis.class);
    private static String baseUrl = "http://www.iyiou.com";

    public static void main(String[] args) {
        IyiouToRedis iyiouToRedis = new IyiouToRedis();
        iyiouToRedis.homepage("https://www.iyiou.com/");
    }
    //首页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "iyiou");
//            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("#nav-industry > li > a,#nav-top > li:nth-child(8) > a");
                if (!categoryList.isEmpty()) {
                    for (Element e : categoryList) {
                        String href = e.attr("href");
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
            String html = HttpUtil.httpGetwithJudgeWord(href, "iyiou");
            Document document = Jsoup.parse(html);
            Element element = document.select("#page-nav > li > a").last();
            String pageCount = element.text();
            String tailpage = element.attr("href");
            int i;
            for (i = 1; i <=Integer.parseInt(pageCount); i++) {
                String links = tailpage.replace(pageCount, String.valueOf(i));
//                System.out.println(links);
                newsList(links);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    //新闻列表
    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "iyiou");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("div.text.fl > a");
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
