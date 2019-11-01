package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class CnpvToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(CnpvToRedis.class);
    private static String baseUrl = "http://www.cnpv.com";


    public static void main(String[] args) {
        CnpvToRedis cnpvToRedis = new CnpvToRedis();
        cnpvToRedis.homepage("http://www.cnpv.com");
    }
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "cnpv");
//            Thread.sleep(2000);
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("a.list-group-item.text-center");
                for (Element e : categoryList) {
                    if (!e.text().contains("资讯快报")) {
                        String href = baseUrl + e.attr("href");
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
            int total = 2857;
            int number;
            for (number = 1; number <= total; number++) {
                String pages = String.valueOf(number);
                String replace = url.replace("1",pages);
                category(replace);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void category(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "cnpv");
            if (null != html) {
//                Thread.sleep(2000);
                Document document = Jsoup.parse(html);
                Elements detailList = document.select("div.news-item-title > a");
                if (0 != detailList.size()) {
                    for (Element e : detailList) {
                        RedisUtil.insertUrlToSet("toCatchUrl",(baseUrl + e.attr("href")));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
