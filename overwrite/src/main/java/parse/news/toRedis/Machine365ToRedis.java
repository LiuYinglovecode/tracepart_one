package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class Machine365ToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(Machine365ToRedis.class);

    //    新闻网首页
    public void homePage(String url) {
        try {
            String get = HttpUtil.httpGetwithJudgeWord(url, "news");
            Document gbk = Jsoup.parse(get);
            Elements select = gbk.select("div.nav ul li a");
            for (Element element : select) {
                if (!"首页".equals(element.text()) && !"技术动态".equals(element.text()) && !"高端访谈".equals(element.text())) {
                    String href = "http://news.machine365.com" + element.attr("href");
                    nextPage(href);
                }

                if ("技术动态".equals(element.text())) {
                    String href = "http://news.machine365.com" + element.attr("href");
                    String html = HttpUtil.httpGetwithJudgeWord(href, "news");
                    Document doc = Jsoup.parse(html);
                    Elements list = doc.select("div.more.tdchnique_more > a");
                    for (Element el : list) {
                        String link = "http://news.machine365.com" + el.attr("href");
                        newsList(link);
                    }
                }
                if ("高端访谈".equals(element.text())) {
                    String href = "http://news.machine365.com" + element.attr("href");
                    String html = HttpUtil.httpGetwithJudgeWord(href, "news");
                    Document doc = Jsoup.parse(html);
                    Elements list = doc.select("div.more.interview_M > a");
                    for (Element el : list) {
                        String link = "http://news.machine365.com" + el.attr("href");
                        newsList(link);
                    }
                }
            }
            LOGGER.info("news.machine365.com  DONE");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //    下一页
    private void nextPage(String href) {
        try {
            String replace = href.replace(".shtml", "");
            int beginPag = 1;
            for (beginPag = 1; beginPag < 5361; beginPag++) {
                String beginpag = replace + "-" + beginPag + ".shtml";
                newsList(beginpag);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    //    新闻列表
    private void newsList(String url) {

        try {
            String get = HttpUtil.httpGetwithJudgeWord(url, "news");
            Document gbk = Jsoup.parse(get);
            Elements select = gbk.select("div.guonei_l div div ul li a");
            for (Element element : select) {
                String href = element.attr("href");
                RedisUtil.insertUrlToSet("toCatchUrl", href);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
