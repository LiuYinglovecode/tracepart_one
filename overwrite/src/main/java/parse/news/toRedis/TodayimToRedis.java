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

public class TodayimToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(TodayimToRedis.class);
    private static String link = new String("http://www.todayim.cn");

    public static void main(String[] args) {
        TodayimToRedis todayimToRedis = new TodayimToRedis();
        todayimToRedis.homepage("http://www.todayim.cn/news/3.html");
    }

    //首页
    public void homepage(String url) {
        try {

            String html = HttpUtil.httpGetwithJudgeWord(url, "todayim");
            Document document = Jsoup.parse(html);
            Elements pages = document.select("div.digg > a.next-page").prev();
            String pageCount = pages.text().trim();
            String tailPage = pages.attr("href");
            int i;
            for (i = 2; i <= Integer.parseInt(pageCount); i++) {
                String links = link.concat(tailPage.replace(pageCount, String.valueOf(i)));
                newsList(links);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    //新闻列表
    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "todayim");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("div.news-pagelist-titleL.fl > a");
                if (!newsListInfo.isEmpty()) {
                    for (Element e : newsListInfo) {
                        String href = link.concat(e.attr("href"));
                        RedisUtil.insertUrlToSet("toCatchUrl", href);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
