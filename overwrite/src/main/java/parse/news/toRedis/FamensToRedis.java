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

import java.util.ArrayList;

public class FamensToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(FamensToRedis.class);
    private static String baseUrl = "http://www.famens.com";

    public static void main(String[] args) {
        FamensToRedis famensToRedis = new FamensToRedis();
        famensToRedis.homepage("http://www.famens.com/News/");
    }
    //首页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "资讯");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.c-menu.clearfix p a");
                for (Element e : categoryList) {
                    String link =baseUrl + e.attr("href");
                    Thread.sleep(SleepUtils.sleepMin());
                    paging(link);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    //分页
    private void paging(String url) {
        try {
            ArrayList<String> list = new ArrayList<>();
            int number = 1;
            String html = HttpUtil.httpGetwithJudgeWord(url, "资讯");
            Document document = Jsoup.parse(html);
            Elements pages = document.select("div#DivPages.pages");
            pages.select("a").remove();
            String Total = pages.text().split("/")[1].replace("页 1", "");
            int total = Integer.valueOf(Total).intValue();
            for (number = 1; number < total + 1; number++) {
                String nextPage = url + number + ".html";
                list.add(nextPage);
            }
            for (String link : list) {
//                System.out.println("下一页：" + link);
                Thread.sleep(SleepUtils.sleepMin());
                newsList(link);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //新闻列表
    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "资讯");
            Document document = Jsoup.parse(html);
            Elements newsListInfo = document.select("li p.tit a");
            for (Element e : newsListInfo) {
                String href = e.attr("href");
                if (href.contains("http")){
                    Thread.sleep(SleepUtils.sleepMin());
                    RedisUtil.insertUrlToSet("toCatchUrl", href);
                }else {
                    Thread.sleep(SleepUtils.sleepMin());
                    RedisUtil.insertUrlToSet("toCatchUrl", baseUrl+href);
                }

            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
