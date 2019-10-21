package parse.news.download;

import Utils.MD5Util;
import Utils.NewsMd5;
import Utils.RedisUtil;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ESUtil;
import util.HttpUtil;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class GkzhanDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(GkzhanDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    //    新闻信息
    public void newsinfo(String url) {
        JSONObject newsInfo = new JSONObject();
        try {
            String get = HttpUtil.httpGetwithJudgeWord(url, "news");
            Document parse = Jsoup.parse(get);
            newsInfo.put("plate", parse.select("div.position > p > span").text().trim());
            String title = parse.select("div.leftTop.clearfix > h2").text().trim();
            newsInfo.put("title", title);
            newsInfo.put("time", parse.select("div.leftTop.clearfix > p > span:nth-child(1)").text().trim());
            Elements text = parse.select("#newsContent");
            newsInfo.put("text", text.html());
            String newsId = NewsMd5.newsMd5(text.text().trim());
            newsInfo.put("newsId", newsId);
            Elements list = parse.select("div.leftTop.clearfix > p > span");
            for (Element element : list) {
                if (element.text().contains("来源：")) {
                    newsInfo.put("source", element.text().trim().split("：", 2)[1]);
                } else if (element.text().contains("编辑：")) {
                    newsInfo.put("author", element.text().trim().split("：", 2)[1]);
                } else if (element.text().contains("阅读量：")) {
                    newsInfo.put("amountOfReading", element.text().trim().split("：", 2)[1]);
                }
            }
            newsInfo.put("crawlerId", "33");
            newsInfo.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            newsInfo.put("@timestamp", timestamp2.format(new Date()));
            newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//            mysqlUtil.insertNews(newsInfo, "crawler_news", newsId);
////            esUtil.writeToES(newsInfo, "crawler-news-", "doc", newsId);
//            if (esUtil.writeToES(newsInfo, "crawler-news-", "doc", newsId)){
//                RedisUtil.insertUrlToSet("catchedUrl", url);
//            }
            if (mysqlUtil.insertNews(newsInfo, "crawler_news", newsId)){
                RedisUtil.insertUrlToSet("catchedUrl", url);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
