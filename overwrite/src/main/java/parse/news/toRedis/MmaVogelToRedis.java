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

public class MmaVogelToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(MmaVogelToRedis.class);


    public static void main(String[] args) {
        MmaVogelToRedis mmVogelToRedis = new MmaVogelToRedis();
        mmVogelToRedis.listNews("https://mma.vogel.com.cn/news_list.html?clsid=1017");
    }

    public void listNews(String url) {
        try {
            for (int i = 1; i <= 2736; i++) {
                String link = url.concat("&pn=").concat(String.valueOf(i));
                String html = HttpUtil.httpGetwithJudgeWord(link, "vogel");
                Thread.sleep(SleepUtils.sleepMin());
                if (!html.isEmpty()) {
                    Document document = Jsoup.parse(html);
                    Elements elements = document.select("div.txt-block > h3 > a");
                    if (!elements.isEmpty()) {
                        for (Element element : elements) {
                            String href = element.attr("href");
                            RedisUtil.insertUrlToSet("toCatchUrl", href);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
