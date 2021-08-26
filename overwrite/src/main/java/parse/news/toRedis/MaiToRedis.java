package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class MaiToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(MaiToRedis.class);

    public static void main(String[] args) {
        MaiToRedis maiToRedis = new MaiToRedis();
        maiToRedis.homepage("http://www.86mai.com/news/");
    }
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "86mai");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.head-txt > span > a");
                for (Element e : categoryList) {
                    String href = e.attr("href");
                    paging(href);
                }

            } else {
                LOGGER.info("homepage null");
            }
            LOGGER.info("www.86mai.com  DONE");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页
    private void paging(String url) {
        try {
            String replace = url.replace(".html", "");
            String html = HttpUtil.httpGetwithJudgeWord(url, "86mai");
            Document parse = Jsoup.parse(html);
            String pagesNumber = parse.select("div.pages cite").text().split("/")[1].replace("页", "");//获取总结数
            int total = Integer.valueOf(pagesNumber).intValue();//类型转换
            int number = 1;
            for (number = 1; number <= total; number++) {
                String link = replace + "_" + number + ".html";//拼接链接地址
                newsList(link);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "86mai");
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("div.catlist > ul > li> a");
            for (Element e : select) {
                String link = e.attr("href");
                RedisUtil.insertUrlToSet("toCatchUrl", link);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
