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

public class AilabToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(AilabToRedis.class);

    public static void main(String[] args) {
        AilabToRedis ailabToRedis = new AilabToRedis();
        ailabToRedis.homepage("http://www.ailab.cn/");

    }

    //首页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "ailab");
//            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements tailPage = document.select("div.pg > a.last");
                String pageCount = tailPage.attr("href").split("=")[1].trim();
                String attr = tailPage.attr("href");
                int i;
                for (i = 1; i <= Integer.parseInt(pageCount); i++) {
                    String replace = attr.replace(pageCount, String.valueOf(i));
                    newsList(replace);
                }
            } else {
                LOGGER.info("null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //新闻列表
    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "ailab");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("ul.list_jc > li > a.title");
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
