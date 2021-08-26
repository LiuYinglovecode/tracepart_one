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

public class IlinkiToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(IlinkiToRedis.class);
    private static String links = new String("http://www.ilinki.net");

    public static void main(String[] args) {
        IlinkiToRedis ilinkiToRedis = new IlinkiToRedis();
        ilinkiToRedis.homepage("http://www.ilinki.net/");

    }


    //首页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements newsList = document.select("ul.index_nav_list > a");
                if (!newsList.isEmpty()) {
                    for (Element e : newsList) {
                        if (e.select("li").text().contains("新闻") || e.select("li").text().contains("动态")) {
                            String href = links.concat(e.attr("href"));
                            paging(href);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void paging(String href) {
        try {
            String judgeWord = HttpUtil.httpGetwithJudgeWord(href, "ilinki");
            Thread.sleep(SleepUtils.sleepMin());
            Document document = Jsoup.parse(judgeWord);
            Elements select = document.select("li.last_page > a");
            String tailPage = select.attr("href");
            String pages = select.attr("href").split("page=")[1];
            for (int i = 1; i <= Integer.parseInt(pages); i++) {
                String link = links.concat(tailPage.replace(pages, String.valueOf(i)));
                newsList(link);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }


    }

    //新闻列表
    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "ilinki");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("p.list_title > a");
                if (!newsListInfo.isEmpty()) {
                    for (Element e : newsListInfo) {
                        String href = links.concat(e.attr("href"));
                        RedisUtil.insertUrlToSet("toCatchUrl", href);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
