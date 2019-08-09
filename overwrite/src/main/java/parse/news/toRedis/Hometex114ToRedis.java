package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

import java.net.URL;
import java.util.ArrayList;

public class Hometex114ToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(Hometex114ToRedis.class);
    private static String baseUrl = "http://www.hometex114.com";
    //主页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "hometex114");
            if (null != html) {
                Document document = Jsoup.parse(new URL(url).openStream(), "GBK",html);
                Elements categoryList = document.select("div.item > h2 > a");
                for (Element element : categoryList) {
                    String href =baseUrl + element.attr("href");
                    paging(href);
                }
            }
            LOGGER.info("pages null！");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页
    private void paging(String url) {
        String replace = url.replace("1.html", "");
        try {
            ArrayList<String> list = new ArrayList<>();
            int number = 1;
            String html = HttpUtil.httpGetwithJudgeWord(url, "hometex114");
            Document document = Jsoup.parse(new URL(url).openStream(), "GBK",html);
            String Total = document.select("div.item > ul > li").text().split(" 个")[0].split("计 ")[1];
            int total = Integer.parseInt(Total);
            for (number = 1; number <= total ; number++) {
                String nextPage = replace + number + ".html";
                list.add(nextPage);
            }
            for (String link : list) {
                newsList(link);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //新闻列表
    private void newsList(String url) {
        ArrayList<String> list = new ArrayList<>();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "hometex114");
            if (html != null) {
                Document document = Jsoup.parse(new URL(url).openStream(), "GBK",html);
                Elements newsListInfo = document.select("div > h2 > a");
                for (Element e : newsListInfo) {
                    String href = e.attr("href").contains("http") ? e.attr("href") : baseUrl + e.attr("href");
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
