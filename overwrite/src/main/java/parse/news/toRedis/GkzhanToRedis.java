package parse.news.toRedis;

import Utils.RedisUtil;
import news.parse.gkzhanNews;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

public class GkzhanToRedis {
    private final static Logger LOGGER = LoggerFactory.getLogger(GkzhanToRedis.class);

    //  新闻网首页
    public void homePage(String url) {
        try {
            String get = HttpUtil.httpGetwithJudgeWord(url, "news");
            Document html = Jsoup.parse(get);
            Elements select = html.select("#nav > div > ul > li > a");
            for (Element element : select) {
                if (!"新闻首页".equals(element.text())) {
                    String href = element.attr("href");
                    newsList(href);
                }
            }
            LOGGER.info("www.gkzhan.com DONE");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //    新闻列表及分页
    private void newsList(String url) {
        try {
            String get = HttpUtil.httpGetwithJudgeWord(url, "news");
            Document html = Jsoup.parse(get);
            Elements select = html.select("div.listLeft > div > h3 > a");
            for (Element element : select) {
                String href = element.attr("href");
                RedisUtil.insertUrlToSet("toCatchUrl", href);
            }

            Elements nextPage = html.select("a.lt");
            String href = nextPage.attr("href");
            if (!"#".equals(href)) {
                newsList("https://www.gkzhan.com" + href);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

    }
}
