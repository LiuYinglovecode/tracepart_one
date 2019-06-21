package parse.news.download;

import Utils.MD5Util;
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
                String author = document.select(".time").text().trim().split("/", 2)[0];
                String time = document.select(".time").text().trim().split("/", 2)[1].split("：", 2)[1];
                String text = document.select(".newstext").text().trim();
                String newId = MD5Util.getMD5String(text);
                String url = detailUrl;
                Elements imgList = document.select(".newstext img");
                for (Element e : imgList) {
                    imgs.add(baseUrl + e.attr("src"));
                }
                info.put("newId",newId);
                info.put("title", title);
                info.put("author", author);
                info.put("time", time);
                info.put("text", text);
                info.put("url", url);
                info.put("images", imgs.toString());
                info.put("crawlerId", "50");
                info.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                info.put("@timestamp", timestamp2.format(new Date()));
                info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                mysqlUtil.insertNews(info, "crawler_news", newId);
                esUtil.writeToES(info, "crawler-news-", "doc");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
