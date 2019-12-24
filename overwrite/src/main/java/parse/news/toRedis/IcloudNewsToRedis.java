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

public class IcloudNewsToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(IcloudNewsToRedis.class);

    public static void main(String[] args) {
        IcloudNewsToRedis icloudNewsToRedis = new IcloudNewsToRedis();
        icloudNewsToRedis.homepage("https://www.icloudnews.net/");
    }

    //首页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "icloudnews");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements newsList = document.select("div.col_tright > a");
                if (!newsList.isEmpty()) {
                    for (Element e : newsList) {
                        String href =e.attr("href");
                        paging(href);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void paging(String href) {
        try {
            String judgeWord = HttpUtil.httpGetwithJudgeWord(href, "icloudnews");
            Thread.sleep(SleepUtils.sleepMin());
            Document document = Jsoup.parse(judgeWord);
            Element select = document.select(".page > a").last();
            String tailPage = select.attr("href");
            String pages = tailPage.split("_")[1].replace(".html","");
            for (int i = 1; i <= Integer.parseInt(pages); i++) {
                String link = tailPage.replace(pages, String.valueOf(i));
                newsList(link);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }


    }

    //新闻列表
    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "icloudnews");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("p.ptitle > a");
                if (!newsListInfo.isEmpty()) {
                    for (Element e : newsListInfo) {
                        String href = e.attr("href");
                        RedisUtil.insertUrlToSet("toCatchUrl", href);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
