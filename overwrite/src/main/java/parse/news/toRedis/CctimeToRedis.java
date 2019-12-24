package parse.news.toRedis;

import Utils.HttpUtil;
import Utils.RedisUtil;
import Utils.SleepUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CctimeToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(CctimeToRedis.class);

    public static void main(String[] args) {
        CctimeToRedis cctimeToRedis = new CctimeToRedis();
        cctimeToRedis.homePage("http://www.cctime.com/");
    }
    public void homePage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "飞象网");
            Thread.sleep(SleepUtils.sleepMin());
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categories = document.select("#bidu_title > a,#kuaixun_title > a");
                for (Element e : categories) {
                    String link = e.attr("href");
                    for (int i = 1; i <= 50; i++) {
                        String links = link.replace(".htm", "").concat("-").concat(String.valueOf(i)).concat(".htm");
                        newsList(links);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void newsList(String links) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(links, "飞象网");
            Thread.sleep(SleepUtils.sleepMin());
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements urlList = document.select("div.kcs_list > h2 > a");
                for (Element e : urlList) {
                    RedisUtil.insertUrlToSet("toCatchUrl", e.attr("href"));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
