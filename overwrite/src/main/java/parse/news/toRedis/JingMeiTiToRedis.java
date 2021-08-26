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

public class JingMeiTiToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(JingMeiTiToRedis.class);

    public static void main(String[] args) {
        JingMeiTiToRedis jingMeiTiToRedis = new JingMeiTiToRedis();
        jingMeiTiToRedis.homepage("http://www.jingmeiti.com/");
    }
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "jingmeiti");
//            Thread.sleep(SleepUtils.sleepMin());
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("#menu-navigation > li > a");
                for (Element e : categoryList) {
                    if (e.text().contains("资讯")||e.text().contains("行业研究")) {
                        String href = e.attr("href");
                        paging(href);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页
    private void paging(String url) {
        try {
            String s = HttpUtil.httpGetwithJudgeWord(url, "jingmeiti");
            Document document = Jsoup.parse(s);
            Elements elements = document.select("a.next.page-numbers").prev();
            String text = elements.text();
            String attr = elements.attr("href");
            for (int i = 1; i <=Integer.parseInt(text) ; i++) {
                String links = attr.replace(text, String.valueOf(i));
                newsList(links);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "jingmeiti");
            if (html != null) {
                Document parse = Jsoup.parse(html);
                    Elements select = parse.select("div.posts-default-title > h2 > a");
                    for (Element e : select) {
                        String link = e.attr("href");
                        RedisUtil.insertUrlToSet("toCatchUrl", link);
                    }
                }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
