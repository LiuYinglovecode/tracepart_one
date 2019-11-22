package parse.news.toRedis;

import Utils.HtmlUnitUnits;
import Utils.HttpUtil;
import Utils.RedisUtil;
import Utils.SleepUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZhiNengWangToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZhiNengWangToRedis.class);
    private static String links = new String("http://www.2025china.cn");


    public static void main(String[] args) {
        ZhiNengWangToRedis zhiNengWangToRedis = new ZhiNengWangToRedis();
        zhiNengWangToRedis.listNews("http://www.2025china.cn/sec/hotnews");
    }

    public void listNews(String url) {
        try {
            for (int i = 1; i <=517 ; i++) {
                Thread.sleep(1000);
                String link = url.concat("/?page=").concat(String.valueOf(i));
                String html = HttpUtil.httpGetwithJudgeWord(link, "");
                Thread.sleep(SleepUtils.sleepMin());
                if (!html.isEmpty()) {
                    Document document = Jsoup.parse(html);
                    Elements elements = document.select("div.info_item.clearfix > h3 > a");
                    if (!elements.isEmpty()) {
                        for (Element element : elements) {
                            String href = links.concat(element.attr("href"));
                            RedisUtil.insertUrlToSet("toCatchUrl", href);
                        }
                    }
                }else {
                    LOGGER.info("链接超时");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
