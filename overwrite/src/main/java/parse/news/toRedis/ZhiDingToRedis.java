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

public class ZhiDingToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZhiDingToRedis.class);
    private static String links = new String("http://www.zhiding.cn");


    public static void main(String[] args) {
        ZhiDingToRedis zhiDingToRedis = new ZhiDingToRedis();
        zhiDingToRedis.homepage("http://www.zhiding.cn/lists-0-1-1-0-0.htm");
    }

    public void homepage(String url) {
        try {
            String zhiding = HttpUtil.httpGetwithJudgeWord(url, "zhiding");
            Document document = Jsoup.parse(zhiding);
            Elements pageCount = document.select("div.paging > ul > li > a").eq(7);
            String attr = pageCount.attr("href");
            String s = attr.split("1-")[1].replace("-0-0.htm","");
            for (int i = 1; i < Integer.parseInt(s); i++) {
                String link = links.concat(attr.replace(s, String.valueOf(i)));
                listNews(link);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void listNews(String link) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(link, "zhiding");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements elements = document.select("div.right > h3 > a");
                if (!elements.isEmpty()) {
                    for (Element element : elements) {
                        String href = element.attr("href");
                        RedisUtil.insertUrlToSet("toCatchUrl", href);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
