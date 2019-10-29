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

public class CenaToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(CenaToRedis.class);
    private static String link = new String("http://www.cena.com.cn");

    public static void main(String[] args) {
        CenaToRedis cenaToRedis = new CenaToRedis();
        cenaToRedis.homepage("http://www.cena.com.cn/");
    }
    //首页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国电子报");
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("ul.navigation > li > a");
                for (Element element : categoryList) {
                    if (!element.text().contains("首页")
                        &&!element.text().contains("会议活动")
                        &&!element.text().contains("特别策划")
                        &&!element.text().contains("视频")) {
                        String href = element.attr("href");
//                        System.out.println(href);
                        paging(href);
                    }
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
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国电子报");
            Document document = Jsoup.parse(html);
            Elements element = document.select("#displaypagenum > ul > li.disabled").next();
            String total = element.text();
            String end = element.select("a").attr("href");

            for (number = 1; number <=Integer.parseInt(total); number++) {
                String nextPage = link.concat(end).replace(total,String.valueOf(number));
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
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国电子报");
            Document document = Jsoup.parse(html);
            Elements newsListInfo = document.select("div.news_w > h2 > a");
            for (Element e : newsListInfo) {
                String href = e.attr("href");
                RedisUtil.insertUrlToSet("toCatchUrl", href);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

    }
}
