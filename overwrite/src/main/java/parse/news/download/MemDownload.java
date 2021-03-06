package parse.news.download;

import Utils.ForMat;
import Utils.NewsMd5;
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
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MemDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();
    private static String baseUrl = "http://www.membranes.com.cn";

    public void newsInfo(String detailUrl) {
        try {
            JSONObject info = new JSONObject();
            JSONArray imgs = new JSONArray();
            String html = HttpUtil.httpGetwithJudgeWord(detailUrl, "中国膜工业协会");
            if (null != html) {
                Document document = Jsoup.parse(html);
                String title = document.select(".title").text().trim();
                String author = document.select(".time").text().split("/", 2)[0].trim();
                String time = document.select(".time").text().split("/", 2)[1].split("：", 2)[1].trim();
                Elements text = document.select(".newstext");
                String newsId = NewsMd5.newsMd5(text.text().trim());
                String url = detailUrl;
                Elements imgList = document.select(".newstext img");
                for (Element e : imgList) {
                    imgs.add(baseUrl + e.attr("src"));
                }
                info.put("newId", newsId);
                info.put("title", title);
                info.put("author", author);
                info.put("time", ForMat.getDatetimeFormat(time));
                info.put("text", text.text().trim());
                info.put("html", text.html());
                info.put("url", url);
                info.put("images", imgs.toString());
                info.put("crawlerId", "50");
                info.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                info.put("@timestamp", timestamp2.format(new Date()));
                info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                mysqlUtil.insertNews(info, "crawler_news", newId);
////                esUtil.writeToES(info, "crawler-news-", "doc", newId);
//                if (esUtil.writeToES(info, "crawler-news-", "doc", newId)){
//                    RedisUtil.insertUrlToSet("catchedUrl", url);
//                }
                if (mysqlUtil.insertNews(info)){
                    RedisUtil.insertUrlToSet("catchedUrl", url);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
