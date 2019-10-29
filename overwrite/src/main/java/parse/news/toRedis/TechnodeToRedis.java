package parse.news.toRedis;

import Utils.RedisUtil;
import Utils.SleepUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TechnodeToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(TechnodeToRedis.class);


    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "相关站点");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("#menu-item-217285 a,#menu-item-215594 > a");
                for (Element e : categoryList) {
                    String href =e.attr("href");
                    ping(href);

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
            String html = HttpUtil.httpGetwithJudgeWord(url, "相关站点");
            Thread.sleep(SleepUtils.sleepMin());
            Document doc = Jsoup.parse(html);
            String tailPage = doc.select("li.page-next").prev().select("span a").attr("href");
            Pattern compile = Pattern.compile("[^0-9]");
            Matcher matcher = compile.matcher(tailPage);
            String pageCount = matcher.replaceAll("").trim();
            int i = Integer.parseInt(pageCount);
            for (int j = 1; j <= i; j++) {
                String s = String.valueOf(j);
                String link = tailPage.replace(pageCount, s);
                newsList(link);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void newsList(String link) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(link, "相关站点");
            Thread.sleep(SleepUtils.sleepMin());
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements detailList = document.select("div.t-entry-text > div > div > h3 > a");
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
