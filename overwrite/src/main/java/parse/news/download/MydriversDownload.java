package parse.news.download;

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
import util.HttpUtil;
import util.MD5Util;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MydriversDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(MydriversDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject newsInfo = new JSONObject();
        newsInfo.put("url", url);
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "mydrivers");
            Document document = Jsoup.parse(html);
            newsInfo.put("title", document.select("#thread_subject").text().trim());
            Elements select = document.select("div.news_bt1_left");
            if (select.text().contains("出处：") && select.text().contains("作者：")
                    && select.text().contains("编辑：") && select.text().contains("人气：")) {
                newsInfo.put("time", select.text().split("出处：")[0].trim());
                newsInfo.put("source", select.text().split("出处：")[1].split("作者：")[0]);
                newsInfo.put("author", select.text().split("作者：")[1].split("编辑：")[0]);
            }
            Elements amountOfReading = document.select("#Hits > font");
            if (amountOfReading.size()!=0) {
                newsInfo.put("amountOfReading",amountOfReading.text());
            }

            Elements text = document.select("div.news_info > p");
            newsInfo.put("text", text.text());
            String md5String = MD5Util.getMD5String(text.text());
            newsInfo.put("newsId", md5String);
            Elements src = text.select("img");
            if (src.size() != 0) {
                for (Element element : src) {
                    imgsList.add("http:"+element.attr("src"));
                }
                newsInfo.put("images", imgsList.toString());
            }

            newsInfo.put("crawlerId", "107");
            newsInfo.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            newsInfo.put("@timestamp", timestamp2.format(new Date()));
            newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
            mysqlUtil.insertNews(newsInfo, "crawler_news", md5String);
            if (esUtil.writeToES(newsInfo, "crawler-news-", "doc", md5String)) {
                RedisUtil.insertUrlToSet("catchedUrl", url);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
