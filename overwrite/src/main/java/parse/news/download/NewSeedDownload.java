package parse.news.download;

import Utils.NewsMd5;
import Utils.RedisUtil;
import Utils.SleepUtils;
import com.alibaba.fastjson.JSONArray;
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

public class NewSeedDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewSeedDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void newsInfo(String url) {
        try {
            String newsId =null;
            String html = HttpUtil.httpGetwithJudgeWord(url, "新芽");
            Thread.sleep(SleepUtils.sleepMin());
            if (null != html) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                Document document = Jsoup.parse(html);
                info.put("url", url);
                info.put("title", document.select("#title").text().trim());
                String source = document.select("div.news-show-title > div > span.resfrom").text().trim();
                if (source.contains("：")){
                    info.put("source",source.split("：")[1].trim());
                }else {
                    info.put("source",source.trim());
                }

                Elements author = document.select("div.news-show-title > div > span.author");
                if (0!=author.size()){
                    info.put("author",author.text().trim());
                }

                Elements date = document.select("div.news-show-title > div > span.date");
                if (0!=date.size()){
                    info.put("time",date.text().trim());
                }

                Elements plate = document.select("div.news-show-title > div > span.dot");
                if (0!=plate.size()){
                    info.put("plate",plate.text().replace(" · · ·","").trim());
                }

                Elements text = document.select("#news-content");
                if (0!=text.size()){
                    info.put("text",text.html());
                     newsId = NewsMd5.newsMd5(text.text().trim());
                }
                info.put("newsId",newsId);

                info.put("crawlerId", "109");
                info.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                info.put("@timestamp", timestamp2.format(new Date()));
                info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                mysqlUtil.insertNews(info, "crawler_news", newsId);
//                if (esUtil.writeToES(info, "crawler-news-", "doc", newsId)){
//                    RedisUtil.insertUrlToSet("catchedUrl", url);
//                }
                if (mysqlUtil.insertNews(info, "crawler_news", newsId)){
                    RedisUtil.insertUrlToSet("catchedUrl", url);
                }
            } else {
                LOGGER.info("detail null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
