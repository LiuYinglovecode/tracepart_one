package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

public class TechwebToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(GongkongToRedis.class);
    private static String baseUrl = "http://www.techweb.com.cn";


    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于本站");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.headnav > ul > li > a");
                for (Element e : categoryList) {
                    if (!e.text().contains("首页") && !e.text().contains("游戏") && !e.text().contains("更多")) {
                        String href =e.attr("href");
//                        System.out.println(href);
//                        Thread.sleep(2000);
                        ping(href);
                    }
                }
            } else {
                LOGGER.info("homepage null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void ping(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于本站");
            Document doc = Jsoup.parse(html);
            String first = doc.select("div.page > a").first().attr("href");
            String select = doc.select("div.page > a").last().attr("href").split("_")[1].replace(".shtml#wp","");
            if (null != select) {
                int count = Integer.parseInt(select);
                int number;
                for (number = 1; number <= count; number++) {
                    String s = String.valueOf(number);
                    String link = baseUrl + first.replace("2",s);
//                    System.out.println(link);
                    Thread.sleep(2000);
                    category(link);
                }
            }else {
                category(url);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void category(String link) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(link, "关于本站");
//            Thread.sleep(2000);
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements detailList = document.select("div.news_title > h3 > a");
                if (0 != detailList.size()) {
                    for (Element e : detailList) {
                        RedisUtil.insertUrlToSet("toCatchUrl",(e.attr("href")));
                    }
                }else {
                    LOGGER.info("该页面为空");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
