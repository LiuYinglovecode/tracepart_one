package parse.news.toRedis;

import Utils.HtmlUnitUnits;
import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class QqTechToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(QqTechToRedis.class);
    private static String baseUrl = "https://new.qq.com";


    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "qq");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("#Left > div > div.bd > ul > li > a");
                for (Element e : categoryList) {
                    if (e.attr("href").contains("http")) {
                        String href = e.attr("href");
                        listNews(href);
                    } else {
                        String href = baseUrl+e.attr("href");
                        listNews(href);
                    }
                }
            } else {
                LOGGER.info("homepage null");
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void listNews(String link) {
        try {
            HtmlUnitUnits httpUtils = HtmlUnitUnits.getInstance();
            httpUtils.setTimeout(30000);
            httpUtils.setWaitForBackgroundJavaScript(30000);
            Document document = httpUtils.getHtmlPageResponseAsDocument(link);
            Elements detailList = document.select(".cf > h3 > a");
            if (0 != detailList.size()) {
                for (Element e : detailList) {
                    RedisUtil.insertUrlToSet("toCatchUrl", (e.attr("href")));
                }
            } else {
                LOGGER.info("该页面为空");
            }
        } catch (Exception ex) {
            LOGGER.info(ex.getMessage());
        }
    }
}
