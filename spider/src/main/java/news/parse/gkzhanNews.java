package news.parse;

import com.alibaba.fastjson.JSONObject;
import config.IConfigManager;
import news.utils.ESUtil;
import news.utils.mysqlUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;


/**
 * <a>https://www.gkzhan.com/</a>
 * <a>News：智能制造新闻网</a>
 *
 * @author:chenyan
 */
public class gkzhanNews {
    private final static Logger LOGGER = LoggerFactory.getLogger(gkzhanNews.class);
    private static java.util.Map<String, String> Map = null;
    private static java.util.Map<String, String> header;
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();


    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    //    新闻网首页
    private void homePage(String url) {

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
                newsinfo(href);
            }

            Elements nextPage = html.select("a.lt");
            String href = nextPage.attr("href");
            if (!"#".equals(href)) {
                System.out.println("下一页：" + "https://www.gkzhan.com" + href);
                newsList("https://www.gkzhan.com" + href);
            }
            return;


        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

    }

    //    新闻信息
    private void newsinfo(String url) {
        JSONObject newsInfo = new JSONObject();
        try {
            String get = HttpUtil.httpGetwithJudgeWord(url, "news");
            Document parse = Jsoup.parse(get);
            newsInfo.put("plate", parse.select("div.position > p > span").text().trim());
            String title = parse.select("div.leftTop.clearfix > h2").text().trim();
            newsInfo.put("title", title);
            newsInfo.put("time", parse.select("div.leftTop.clearfix > p > span:nth-child(1)").text().trim());
            newsInfo.put("text", parse.select("#newsContent").text().trim());
            Elements list = parse.select("div.leftTop.clearfix > p > span");
            for (Element element : list) {
                if (element.text().contains("来源：")) {
                    newsInfo.put("source", element.text().trim().split("：", 2)[1]);
                } else if (element.text().contains("编辑：")) {
                    newsInfo.put("author", element.text().trim().split("：", 2)[1]);
                } else if (element.text().contains("阅读量：")) {
                    newsInfo.put("amount_of_reading", element.text().trim().split("：", 2)[1]);
                }
            }
            newsInfo.put("crawlerId", "33");
            newsInfo.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            newsInfo.put("@timestamp", timestamp2.format(new Date()));
            newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
            mysqlUtil.insertNews(newsInfo, "crawler_news", title);
            esUtil.writeToES(newsInfo, "crawler-news-", "doc");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "10.153.40.117:2181");
        gkzhanNews gkzhanNews = new gkzhanNews();
        gkzhanNews.homePage("https://www.gkzhan.com/news/");
        LOGGER.info("gkzhanNews DONE :" + String.format("%tF", new Date()) + String.format("%tT", new Date()));

    }
}
