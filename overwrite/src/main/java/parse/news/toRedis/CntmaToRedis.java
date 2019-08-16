package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

import java.net.URL;

public class CntmaToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(CntmaToRedis.class);
    private static String baseUrl = "http://www.cntma.com";

    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "cntma");
            if (null != html) {
                Document document = Jsoup.parse(new URL(url).openStream(), "GBK",html);
                Elements categoryList = document.select("td.A1 > a");
                for (Element e : categoryList) {
                    if (!e.text().contains("资讯首页")&&!e.text().contains("本网")) {
                        String href =baseUrl + e.attr("href");
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
            String html = HttpUtil.httpGetwithJudgeWord(url, "cntma");
            Document doc = Jsoup.parse(new URL(url).openStream(), "GBK",html);
            String select = doc.select("tr:nth-child(31) > td > table > tbody > tr:nth-child(1) > td")
                    .text().split("共计 ")[1].split(" 个页面")[0];
            if (null!=select) {
                int count = Integer.parseInt(select);
                int number;
                for (number = 1; number <= count; number++) {
                    String replace = url+"index"+number+".html";
//                    Thread.sleep(2000);
                    category(replace);
                }
            }else {
                category(url);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void category(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "cntma");
            Thread.sleep(2000);
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements detailList = document.select("table:nth-child(9) > tbody > tr > td:nth-child(1) > table > tbody > tr > td > a");
                if (0 != detailList.size()) {
                    for (Element e : detailList) {
                        RedisUtil.insertUrlToSet("toCatchUrl",(baseUrl + e.attr("href")));
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
