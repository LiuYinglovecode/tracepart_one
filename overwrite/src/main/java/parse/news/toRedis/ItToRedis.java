package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class ItToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItToRedis.class);


    public static void main(String[] args) {
        ItToRedis itToRedis = new ItToRedis();
        itToRedis.homepage("http://www.199it.com/");
    }
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "199it.com/");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("ul.nav-menu.clear li a");
                for (Element e : categoryList) {
                    if (!e.text().contains("首页")) {
                        String href =e.attr("href");
//                        System.out.println(href);
                        Thread.sleep(2000);
                        category(href);
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
            String html = HttpUtil.httpGetwithJudgeWord(url, "下一页");
            if (null != html) {
                Document doc = Jsoup.parse(html);
                String select = doc.select("a.page-numbers")
                        .last()
                        .previousElementSibling()
                        .attr("href")
                        .split("page/")[1]
                        .replace("?from=42","");
                if (null != select) {
                    int count = Integer.parseInt(select);
                    int number;
                    for (number = 1; number <= count; number++) {
                        String s = String.valueOf(number);
                        String link = url + "/page/" + s;
                    Thread.sleep(2000);
                        category(link);
                    }
                }
            } else {
                category(url);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void category(String link) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(link, "199it");
            Thread.sleep(2000);
            if (html != null) {
                Document document = Jsoup.parse(html);
                Elements detailList = document.select(" div.entry-content h2.entry-title a");
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
