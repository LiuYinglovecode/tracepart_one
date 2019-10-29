package parse.news.download;

import Utils.ForMat;
import Utils.RedisUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ESUtil;
import Utils.HttpUtil;
import util.MD5Util;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PedailyDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(PedailyDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject newsInfo = new JSONObject();
        newsInfo.put("url", url);
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "pedaily");
            Document document = Jsoup.parse(html);
            String title = document.select("#newstitle,#title").text().trim();
            newsInfo.put("title", title);
            Elements author = document.select("div.info > div.box-l > span.author,div.news-show-title > div > span.author");
            if (0 != author.size()) {
                newsInfo.put("author", author.text().trim());
            }
            Elements time = document.select("div.info > div.box-l > span.date,div.news-show-title > div > span.date");
            if (0 != time.size()) {
                newsInfo.put("time", ForMat.getDatetimeFormat(time.text().trim()));
            }
            Elements source = document.select("div.info > div.box-l > span.source,div.news-show-title > div > span.source");
            if (0 != source.size()) {
                newsInfo.put("source", source.text().trim());
            }
            Elements text = document.select("#news-content");
            newsInfo.put("text", text.text().trim());
            newsInfo.put("html", text.html());
            String newsId = MD5Util.getMD5String(text.text().trim());
            newsInfo.put("newsId", newsId);
            Elements src = text.select("p img");
            if (src.size() != 0) {
                for (Element element : src) {
                    imgsList.add(element.attr("src"));
                }
                newsInfo.put("images", imgsList.toString());
            }


            newsInfo.put("crawlerId", "105");
            newsInfo.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            newsInfo.put("@timestamp", timestamp2.format(new Date()));
            newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//            mysqlUtil.insertNews(newsInfo, "crawler_news", md5String);
//            if (esUtil.writeToES(newsInfo, "crawler-news-", "doc", md5String)) {
//                RedisUtil.insertUrlToSet("catchedUrl", url);
//            }
            if (mysqlUtil.insertNews(newsInfo)){
                RedisUtil.insertUrlToSet("catchedUrl", url);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
