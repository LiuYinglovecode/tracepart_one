package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

import java.util.ArrayList;

public class ChuandongToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChuandongToRedis.class);

    public static void main(String[] args) {
        ChuandongToRedis chuandongToRedis = new ChuandongToRedis();
        chuandongToRedis.homepage("http://www.chuandong.com/news/");
    }
    //主页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "联系我们");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.hm-nav ul li a");
                for (Element element : categoryList) {
                    if (!element.text().contains("新闻首页")) {
                        String href = element.attr("href");
                        paging(href);
                    }
                }
            }
            LOGGER.info("www.chinahightech.com  DONE");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页
    private void paging(String url) {
        String replace = url.replace("http://www.chuandong.com/news/list.aspx?", "");
        try {
            ArrayList<String> list = new ArrayList<>();
            int number = 1;
            int total = 100;
                for (number = 1; number <= total ; number++) {
                    if (url.contains("http://www.chuandong.com/news/list.aspx?")) {
                        String nextPage = "http://www.chuandong.com/news/list.aspx?" + "page=" + number + "&" + replace;
                        list.add(nextPage);
                    }else {
                        String nextPage = url + "?page=" + number;
                        list.add(nextPage);
                    }
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
            String html = HttpUtil.httpGetwithJudgeWord(url, "联系我们");
            if (html != null) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("div.fr.nl-con h3.nl-con-tit a");
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
