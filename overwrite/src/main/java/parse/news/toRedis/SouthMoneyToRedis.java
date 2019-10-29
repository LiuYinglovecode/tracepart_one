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
import java.io.IOException;
import java.net.URL;


public class SouthMoneyToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(Jc001ToRedis.class);
    private static final String website = new String("http://www.southmoney.com");

    //首页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "southmoney");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("li.first > dl > dd:nth-child(2) > a,li.first > dl > dd:nth-child(4) > a,li.second > dl > dd:nth-child(2) > a:nth-child(2),li.second > dl > dd:nth-child(2) > a:nth-child(3)");
                for (Element e : categoryList) {
                    String href = e.attr("href");
                    paing(href);
                }
            } else {
                LOGGER.info("null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void paing(String href) {

        try {
            String html = HttpUtil.httpGetwithJudgeWord(href, "southmoney");
            Thread.sleep(SleepUtils.sleepMin());
            Document document = Jsoup.parse(html);
            String pageCount = document.select("span.pageinfo")
                    .text()
                    .split("页")[0]
                    .replace("共 ", "");
            String page = document.select("div.pagenation > ul > li > a")
                    .last()
                    .attr("href")
                    .replace("1.html","");
            int i;
            for (i = Integer.parseInt(pageCount); i >= 1; i--) {
                String links = href.concat(page).concat(String.valueOf(i)).concat(".html");
                newsList(links);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

    }

    //新闻列表
    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "southmoney");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("div.col1.fn-left > ul > li > a");
                if (!newsListInfo.isEmpty()) {
                    for (Element e : newsListInfo) {
                        RedisUtil.insertUrlToSet("toCatchUrl", website.concat(e.attr("href")));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
