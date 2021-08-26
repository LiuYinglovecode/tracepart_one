package parse.product.toRedis;

import Utils.RedisUtil;
import Utils.SleepUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class Pe168ProductToRedis {
    private final static Logger LOGGER = LoggerFactory.getLogger(Pe168ProductToRedis.class);

    public void homePage(String url) {
        try {
            String get = HttpUtil.httpGetwithJudgeWord(url, "产品网");
            Thread.sleep(SleepUtils.sleepMin());
            Document gbk = Jsoup.parse(get);
            Elements select = gbk.select("td.catalog_tds > p > a.px15");
            for (Element element : select) {
                String link = element.attr("href");
//                System.out.println(link);
                sort(link);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void sort(String link) {

        try {
            String get = HttpUtil.httpGetwithJudgeWord(link, "产品网");
            Thread.sleep(SleepUtils.sleepMin());
            Document gbk = Jsoup.parse(get);
            Elements select = gbk.select("div.subcats > ul > li > strong > a");
            for (Element element : select) {
                String links = element.attr("href");
//                System.out.println(links);
                nextPage(links);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void nextPage(String links) {
        try {
            String replace = links.replace(".html", "");
            Thread.sleep(SleepUtils.sleepMin());
            String html = HttpUtil.httpGetwithJudgeWord(links, "产品网");
            Document document = Jsoup.parse(html);
            String select = document.select("div.pages > cite").text().split("/")[1].replace("页","");
            int pageCount = Integer.parseInt(select);
            for (int page = 1; page <= pageCount; page++) {
                String link = replace + "-" + page+".html";
    //            System.out.println(link);
                productlist(link);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

    }

    private void productlist(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "pe168");
            Thread.sleep(SleepUtils.sleepMin());
            if (html!=null) {
                Document document = Jsoup.parse(html);
                Elements doc = document.select(".list > table > tbody > tr > td > ul > li > a");
                for (Element element : doc) {
                    if (element.attr("href").contains("http://www.pe168.com")) {
                        String productInfoLink = element.attr("href");
                        RedisUtil.insertUrlToSet("toCatchUrl-Product", productInfoLink);
                    }
                }
            }
        } catch (Exception e){
            LOGGER.error(e.getMessage());
        }
    }
}
