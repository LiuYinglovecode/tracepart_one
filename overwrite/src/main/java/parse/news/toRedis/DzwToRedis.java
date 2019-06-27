package parse.news.toRedis;

import Utils.RedisUtil;
import news.parse.dzwNews;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

import java.net.URL;

public class DzwToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(DzwToRedis.class);

    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "51dzw");
            if (null != html) {
                Document document = Jsoup.parse(new URL(url).openStream(), "GBK", html);
                Elements categoryList = document.select("div.mainLeft > div > a");
                for (Element e : categoryList) {
                    String href = "http://www.51dzw.com" + e.attr("href");
                    String plate = e.text();
                    paging(href, plate);
                }

            } else {
                LOGGER.info("homepage null");
            }
            LOGGER.info("www.51dzw.com DONE");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页
    private void paging(String url, String plate) {
        try {
            String replace = url.replace("1.html", "");
            String html = HttpUtil.httpGetwithJudgeWord(url, "51dzw");
            Document parse = Jsoup.parse(new URL(url).openStream(), "GBK", html);
            String pagesNumber = parse.select("#TechLists > div.page.mt8").text().split("总页数：")[1].split(" 每页记录数：")[0];//获取总结数
            int total = Integer.valueOf(pagesNumber).intValue() + 1;//类型转换
            int number = 1;
            for (number = 1; number < total; number++) {
                String link = replace + number + ".html";//拼接链接地址
                newsList(link, plate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newsList(String url, String plate) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "51dzw");
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("#TechLists > dl > dt > a");
            for (Element e : select) {
                String link = "http://www.51dzw.com" + e.attr("href");
                RedisUtil.insertUrlToSet("toCatchUrl", link);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
