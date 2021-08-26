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

public class JsjxmhwToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsjxmhwToRedis.class);
    private static String link = new String("http://www.jsjxmhw.com/Html/");

    public static void main(String[] args) {
        JsjxmhwToRedis jsjxmhwToRedis = new JsjxmhwToRedis();
        jsjxmhwToRedis.homepage("http://www.jsjxmhw.com/Html/News.asp?SortID=10&SortPath=0,10,");
    }

    //主页
    public void homepage(String url) {
        try {
            for (int i = 1; i <=461 ; i++) {
                String url1 = url.concat("&Page=").concat(String.valueOf(i));
                String html = HttpUtil.httpGetwithJudgeWord(url1, "jsjxmhw");
                Thread.sleep(SleepUtils.sleepMin());
                if (null != html) {
                    Document document = Jsoup.parse(html);
                    Elements categoryList = document.select(" td[colspan] > table > tbody > tr > td > a[title]");
                    for (Element e : categoryList) {
                        String links =link + e.attr("href");
                        RedisUtil.insertUrlToSet("toCatchUrl", links);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
