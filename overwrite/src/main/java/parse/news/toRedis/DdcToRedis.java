package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class DdcToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(DdcToRedis.class);
    private static String baseUrl = "http://news.ddc.net.cn";

    public static void main(String[] args) {
        DdcToRedis ddcToRedis = new DdcToRedis();
        ddcToRedis.homepage("http://news.ddc.net.cn");
    }
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "ddc");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.xiaonav > ul > li > a");
                for (Element e : categoryList) {
                    if (!e.text().contains("资讯首页")) {
                        String href = baseUrl + e.attr("href");
                        ping(href);
                    }
                }
            } else {
                LOGGER.info("homepage null");
            }
            LOGGER.info("news.ddc.net.cn DONE");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void ping(String url) {
        String replace = url.replace(".html", "");
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "ddc");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements doc = document.select("div.listL > ul");
                doc.select("a").remove();
                String Total = doc.text().split("1/")[1].replace("页", "");
                int total = Integer.valueOf(Total).intValue();//转行类型
                int number = 1;
                for (number = 1; number <= total; number++) {
                    String nextPage = replace + "_" + number + ".html";
                    category(nextPage);
                }

            } else {
                LOGGER.info("category null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void category(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "ddc");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements detailList = document.select("div.listdiv > ul > li > div > a");
                for (Element e : detailList) {
                    String href = baseUrl+e.attr("href");
                    RedisUtil.insertUrlToSet("toCatchUrl", href);
                }
            } else {
                LOGGER.info("category null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
