package parse.news.toRedis;

import Utils.RedisUtil;
import Utils.SleepUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

public class TybabaToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(TybabaToRedis.class);
    private static String baseUrl = "https://www.ledinside.cn";

    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "tybaba");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.box_body > table > tbody > tr > td > a");
                if (!categoryList.isEmpty()) {
                    for (Element e : categoryList) {
//                        System.out.println(e.attr("href"));
                        paging(e.attr("href"));
                    }
                }
            } else {
                LOGGER.info("null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void paging(String href) {
        try {
            int nextPage;
            String link = new String(href.replace(".html", ""));
            String html = HttpUtil.httpGetwithJudgeWord(href, "tybaba");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                String pageCount = document.select("cite").text();
                if (!pageCount.isEmpty()) {
                    String number = pageCount.split("/")[1].replace("页", "");
                    for (nextPage = 1; nextPage <= Integer.parseInt(number); nextPage++) {
                        String links = link.concat("-").concat(String.valueOf(nextPage)).concat(".html");
                        listNews(links);
                    }
                }
            } else {
                LOGGER.error("Page exception! Failed parsing!");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void listNews(String links) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(links, "tybaba");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements elements = document.select("div.text-list > h3 > a");
                if (!elements.isEmpty()) {
                    for (Element element : elements) {
                        RedisUtil.insertUrlToSet("toCatchUrl", element.attr("href"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
